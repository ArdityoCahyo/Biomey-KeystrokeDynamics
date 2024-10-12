import json
import numpy as np
import pandas as pd
from scipy.spatial import distance

def convert(json):
    df = pd.read_json(json)

    uid=[]
    sid=[]
    dd = []
    ud = []
    uu = []
    du = []
    end = []
    start = []
    kalimat = []
    duration = []

    for i in range(0,len(df)-1):
        if (df['kalimat'][i] < (df['kalimat'][i+1])) or (df['kalimat'][i] > (df['kalimat'][i+1])):
            i+=1
        else:
            uid.append(df['uid'][i])
            sid.append(df['sid'][i])

            D_1 = df['press'].iloc[i]
            D_2 = df['press'].iloc[i+1]
            diff = D_2 - D_1
            dd.append(diff)

            U = df['release'].iloc[i]
            D = df['press'].iloc[i+1]
            diff = D - U
            ud_int = diff
            ud.append(diff)

            U_1 = df['release'].iloc[i]
            U_2 = df['release'].iloc[i+1]
            diff = U_2 - U_1
            uu.append(diff)

            D = df['press'].iloc[i]
            U = df['release'].iloc[i+1]
            diff = U - D
            du_int = diff
            du.append(diff)

            kalimat.append(df['kalimat'][i])

            key_1 = df['key'].iloc[i]
            key_2 = df['key'].iloc[i+1]
            start.append(key_1)
            end.append(key_2)

            duration.append(du_int - ud_int)

    df_digraf = pd.DataFrame()

    df_digraf['UID'] = uid
    df_digraf['SID'] = sid
    df_digraf['Kalimat'] = kalimat
    df_digraf['Start'] = start
    df_digraf['End'] = end
    df_digraf['DD'] = dd
    df_digraf['UD'] = ud
    df_digraf['UU'] = uu
    df_digraf['DU'] = du
    df_digraf['Duration'] = duration


    return df_digraf.to_json()

import json
import pandas as pd

def feature_extraction(data):
    print("1")
    df = pd.read_json(data)

    print("2")
    DD = create_slice(df,"DD")
    UD = create_slice(df,"UD")
    UU = create_slice(df,"UU")
    DU = create_slice(df,"DU")
    MD = create_slice(df,"Duration")

    print("3")
    Ordered_DD = mean_slice(DD, "DD")
    Ordered_UD = mean_slice(UD, "UD")
    Ordered_UU = mean_slice(UU, "UU")
    Ordered_DU = mean_slice(DU, "DU")
    Ordered_MD = mean_slice(MD, "Duration")


    print("4")
    UA_DD = user_adaptive(Ordered_DD, "DD", 5)
    UA_UD = user_adaptive(Ordered_UD, "UD", 5)
    UA_UU = user_adaptive(Ordered_UU, "UU", 5)
    UA_DU = user_adaptive(Ordered_DU, "DU", 5)
    UA_MD = user_adaptive(Ordered_MD, "Duration", 5)

    print("5")
    DD = user_adaptive_mean(UA_DD, "DD")
    UD = user_adaptive_mean(UA_UD, "UD")
    UU = user_adaptive_mean(UA_UU, "UU")
    DU = user_adaptive_mean(UA_DU, "DU")
    MD = user_adaptive_mean(UA_MD, "Duration")

    print("6")
    # Mengonversi DataFrame ke dictionary
    DD_dict = DD.to_dict(orient='records')
    UD_dict = UD.to_dict(orient='records')
    UU_dict = UU.to_dict(orient='records')
    DU_dict = DU.to_dict(orient='records')
    MD_dict = MD.to_dict(orient='records')


    print("7")
    # Menggabungkan dictionary menjadi satu
    all_data = {
        'DD': DD_dict,
        'UD': UD_dict,
        'UU': UU_dict,
        'DU': DU_dict,
        'MD': MD_dict
    }

    print("8")

    # Mengonversi gabungan dictionary ke JSON
    json_data = json.dumps(all_data, indent=4)

    return json_data

def create_slice(df_di, feature):
    if (feature == 'DD') : return df_di.drop(columns=['UD','UU','DU','Duration'])      #Slice DataFrame DD
    elif (feature == 'UD') : return df_di.drop(columns=['DD','UU','DU','Duration'])    #Slice DataFrame UD
    elif (feature == 'UU') : return df_di.drop(columns=['DD','UD','DU','Duration'])    #Slice DataFrame UU
    elif (feature == 'DU') : return df_di.drop(columns=['DD','UD','UU','Duration'])    #Slice DataFrame DU
    elif (feature == 'Duration') : return df_di.drop(columns=['DD','UD','UU','DU'])    #Slice DataFrame Duration

def mean_slice(df, feature):
    # Menghitung rata-rata untuk setiap grup
    mean_df = df.groupby(['UID', 'SID', 'Start', 'End'], as_index=False)[feature].mean()

    # # Mengurutkan dalam setiap grup berdasarkan 'UID' dan 'SID', dan kemudian mengurutkan kolom 'feature' secara descending
    mean_df = mean_df.sort_values(by=['UID', 'SID', feature], ascending=[True, True, True]).reset_index(drop=True)

    return mean_df

def parting_session(df, feature):
    list = []

    for df in df.groupby(['UID', 'SID']):
        list.append(df[1].sort_values(by=feature))
    return list

def user_adaptive(df, feature, N):
    session_part = []
    session_part = parting_session(df, feature)

    uid = []
    sid = []
    time = []

    for i in range(len(session_part)):
        uid.append(session_part[i]['UID'].values.tolist())
        sid.append(session_part[i]['SID'].values.tolist())
        uid[i] = uid[i][0]
        sid[i] = sid[i][0]
        time.append(np.array_split(session_part[i][feature].values.tolist(), N))

    temp = pd.DataFrame(columns=['UID','SID',feature])
    temp['UID'] = uid
    temp['SID'] = sid
    temp[feature] = time

    return temp

def user_adaptive_mean(df, feature):
    temp = []
    mean_list = []

    for i in range(len(df)):
        for j in range(len(df[feature][i])):
            temp.append(df[feature][i][j].mean())

        mean_list.append(temp)
        temp = []

    df[feature] = mean_list

    return df

def parting_user(df, feature):
    list = []

    for df in df.groupby(['UID']):
        list.append(df[1].sort_values(by=feature))
    return list

def mahalanobis_scenario(train, test, feature):
    print("MHA1")
    distance_values = []
    distance_list = []

    print("MHA2")
    if (len(test) == len(train)):
        print("MHA3")
        for uid in range(len(test)):
            print("MHA4")
            for row in range(len(test[uid])):
                print("MHA5")
                distance_values.append(distance.mahalanobis(test[uid][feature].iloc[row], np.mean(train[uid][feature].values.tolist(), axis=0), np.linalg.pinv(np.cov(train[uid][feature].values.tolist(), rowvar=False))))
            distance_list.append(distance_values)
            distance_values = []

    return distance_list

def mahalanobis(train, test):
    print("1")
    data_train = json.loads(train)
    data_test = json.loads(test)

    print("2")
    DD_train = parting_user(pd.DataFrame(data_train['DD']),"DD")
    UD_train = parting_user(pd.DataFrame(data_train['UD']),"UD")
    UU_train = parting_user(pd.DataFrame(data_train['UU']),"UU")
    DU_train = parting_user(pd.DataFrame(data_train['DU']),"DU")
    MD_train = parting_user(pd.DataFrame(data_train['MD']),"Duration")

    DD_test = parting_user(pd.DataFrame(data_test['DD']),"DD")
    UD_test = parting_user(pd.DataFrame(data_test['UD']),"UD")
    UU_test = parting_user(pd.DataFrame(data_test['UU']),"UU")
    DU_test = parting_user(pd.DataFrame(data_test['DU']),"DU")
    MD_test = parting_user(pd.DataFrame(data_test['MD']),"Duration")

    print('Disini')
    DD_distance = mahalanobis_scenario(DD_train, DD_test, "DD")
    print('Disitu')
    UD_distance = mahalanobis_scenario(UD_train, UD_test, "UD")
    UU_distance = mahalanobis_scenario(UU_train, UU_test, "UU")
    DU_distance = mahalanobis_scenario(DU_train, DU_test, "DU")
    MD_distance = mahalanobis_scenario(MD_train, MD_test, "Duration")

    print("7")
    # Menggabungkan dictionary menjadi satu
    all_data = {
        'DD': DD_distance,
        'UD': UD_distance,
        'UU': UU_distance,
        'DU': DU_distance,
        'MD': MD_distance
    }

    # Mengonversi list of dictionaries ke JSON
    json_data = json.dumps(all_data, indent=4)

    return json_data

def threshold_labeling_scenario(threshold, distance):

    label = []
    temp = []

    for uid in range(len(distance)):
        for index in range(len(distance[uid])):
            if (threshold > distance[uid][index]):
                temp.append(1)
            else:
                temp.append(0)
        label.append(temp)
        temp=[]

    return label

def majority_voting(results):
    # Menghitung jumlah suara untuk setiap posisi
    votes = np.sum(results, axis=0)
    # Jika lebih dari setengah dari jumlah list, maka Genuine (1), jika tidak maka Impostor (0)
    majority_vote = (votes > (results.shape[0] / 2)).astype(int)
    return majority_vote

def fusion_scenario(DD_pred, UD_pred, UU_pred, DU_pred, Duration_pred):
    results = []
    results_fold = []
    results_list = []

    if (len(DD_pred) == len(UD_pred) == len(UU_pred) == len(DU_pred) == len(Duration_pred)):
        for uid in range(len(DD_pred)):
            for row in range(len(DD_pred[uid])):
                # List hasil dari tiap metode
                DD = DD_pred[uid][row]
                UD = UD_pred[uid][row]
                UU = UU_pred[uid][row]
                DU = DU_pred[uid][row]
                Duration = Duration_pred[uid][row]

                # Menggabungkan semua list ke dalam satu array
                results.append(majority_voting(np.array([DU, UD, UU, DD, Duration])))
            results_list.append(results)
            results = []

    return results_list

def predict(threshold, distance):
    data = json.loads(distance)

    DD = data.get('DD', [])
    UD = data.get('UD', [])
    UU = data.get('UU', [])
    DU = data.get('DU', [])
    MD = data.get('MD', [])

    pred_DD = threshold_labeling_scenario(threshold, DD)
    pred_UD = threshold_labeling_scenario(threshold, UD)
    pred_UU = threshold_labeling_scenario(threshold, UU)
    pred_DU = threshold_labeling_scenario(threshold, DU)
    pred_MD = threshold_labeling_scenario(threshold, MD)

    pred_FS = fusion_scenario(pred_DD,pred_UD,pred_UU,pred_DU,pred_MD)

    return pred_FS








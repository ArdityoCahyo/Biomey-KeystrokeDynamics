def buah(nama):
    buahan = ["Apel", "Jeruk", "Pisang"]
    buahan.append(nama)
    buahan.append(concat("0"))
    return buahan

def sayur(nama):
    sayuran = ["Sawi", "Timun", "Tomat"]
    sayuran.append(nama)
    sayuran.append(concat("1"))
    return sayuran

def concat(nama):
    if (nama == "0") : return "Buahan"
    elif (nama == "1") : return "Mayur"
package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums;

public enum FelhasznaloSzerepkor {
    FELHASZNALO, ADMIN, ALKALMAZOTT;

    public String felhatalmazottak(){
        return "ROLE_" + name();
    }
}

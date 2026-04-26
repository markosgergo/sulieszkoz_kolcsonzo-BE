package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.enums;

public enum KolcsonzesStatuszEnum {
    KIADASRA_VAR,   // diák kérte, de még nem hagyta jóvá alkalmazott/admin
    FOGLALVA,       // lefoglalva de még nincs átvéve
    KIKOLCSONOZVE,  // diák által átvéve (alkalmazott/admin jóváhagyta)
    VISSZAADVA,     // visszadva, minden rendben
    KESESBEN        // lejárt a határidő, de még nem hozta vissza
}
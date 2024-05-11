Üdvözletem bárki is legyél!
Hogy megkönnyítsem javítási procedúrádat az alábbiakban írom, hogy szerintem mire hány pontot kéne kapnom, és melyik elemet hol lehet megtalálni. (Ettől még a Te felelősséged meggyőződni, hogy minden valóban működik-e, egyet értsez-e a az én pontozásommal!):

Fordítási hiba nincs: 1

Futtatási hiba nincs: 1

"Firebase autentikáció meg van valósítva. Be lehet jelentkezni és regisztrálni : 4

Adatmodell definiálása (class vagy interfész formájában) : 2

Legalább 3 különböző activity vagy fragmens használata : 2

Beviteli mezők beviteli típusa megfelelő (jelszó kicsillagozva, email-nél megfelelő billentyűzet jelenik meg stb.) : 3

ConstraintLayout és még egy másik layout típus használata : 1

Reszponzív: 
- különböző kijelző méreteken is jól jelennek meg a GUI elemek (akár tableten is)
- elforgatás esetén is igényes marad a layout" : 2 (Használható, de nem minden helyen igényes)

Legalább 2 különböző animáció használata: 2 (fadein a MainActivity-ben és slode_in_row MatchListActivity-ben)

Intentek használata: navigáció meg van valósítva az activityk/fragmensek között (minden activity/fragmens elérhető) : 2

Legalább egy Lifecycle Hook használata a teljes projektben:
- onCreate nem számít
- az alkalmazás funkcionalitásába értelmes módon beágyazott, azaz pl. nem csak egy logolás : 2 (MainActivity OnPause: Animáció+Toast)

Legalább egy olyan androidos erőforrás használata, amihez kell android permission : 1 (Kamera)
Legalább egy notification vagy alam manager vagy job scheduler használata : 2 (Notification, ha kosárba rakunk valamit)

CRUD műveletek mindegyike megvalósult és az adatbázis műveletek a konvenciónak megfelelően külön szálon történnek : 3 (Felhasználó létrahozása, meccsek olvasása és törlése. A törlés kicsit bugos, először kosárba kell tenni, csak utána lehet törölni.)

"Legalább 2 komplex Firestore lekérdezés megvalósítása amely indexet igényel (ide tartoznak: where feltétel, rendezés, léptetés, limitálás) : 4 (Egy orderby, mely a meccseket rendezi aszerint, hogy hányszor lettek a kosárba rakva+egy whereEqualTo, a Júniusi meccsekre)

Szubjektív pontozás a projekt egészére vonatkozólag:
ez 5-ről indul és le lehet vonni, ha igénytelen, összecsapott, látszik hogy nem foglalkozott vele, kísértetiesen hasonlít a videóban létrehozotthoz stb. : Ez a te feladatod :) (Mint látszik, az alkalmazás ERŐS inspirációt szerzett a videókban is létrehozottól, de számos eltérés is van)

Remélem ezzel segíthettem kicsit, további szép napot!

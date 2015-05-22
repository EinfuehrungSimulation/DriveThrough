# DriveThrough
Simulation mit Desmo-j

Discrete Event based Simulation.

Entity
__________________________________________________________
Drive Through:
  Öffnungszeiten
  BestellSchalter
  AusgabeSchalter

Autos:
  Länge
  Geduld        //wenn viele Autos anstehen kehrt das Auto um
  Bestellung    

Schalter:
  Queue
  Länge
  durchschnittliche Bearbeitungszeit
  
Vorrat:         //wenn sie Bestellung nicht vorrätig ist muss sie erst produziert werden

External Events
___________________________________________________________
- Autos müssen erstellt werden        // abhängig von Tageszeit

Events
___________________________________________________________
- Vorräte müssen erstellt werden wenn eine bestimmte Grenze unterschritten wird
- Bestell Vorgang
- Ausgabe Vorgang

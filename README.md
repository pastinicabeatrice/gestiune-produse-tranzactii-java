# Gestiune produse și tranzacții

Aplicație de consolă care citește produse și tranzacții din fișiere, calculează stocurile și generează un raport.

## Funcționalități

- citirea produselor din `src/produse (1).txt` și a tranzacțiilor (intrări/ieșiri) din `src/tranzactii.json`
- calculul stocului pentru fiecare produs
- numărul de produse și lista produselor în ordine alfabetică
- raport scris în `src/lista.txt`
- valoarea totală a stocurilor
- un server TCP pe portul 8080, pornit pe un fir separat, și un client care îl interoghează
- fișierele de test sunt create automat dacă lipsesc

## Rulare

Se deschide folderul în IntelliJ IDEA și se rulează `GestiuneProduse`. Directorul de lucru trebuie să fie
rădăcina proiectului, pentru că fișierele sunt citite din `src/`.

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class GestiuneProduse {
    static class Produs {
        int cod;
        String denumire;
        double pret;

        public Produs(int cod, String denumire, double pret) {
            this.cod = cod;
            this.denumire = denumire;
            this.pret = pret;
        }
    }

    static class Tranzactie {
        int codProdus;
        int cantitate;
        String tip;

        public Tranzactie(int codProdus, int cantitate, String tip) {
            this.codProdus = codProdus;
            this.cantitate = cantitate;
            this.tip = tip;
        }
    }

    private static List<Produs> produse = new ArrayList<>();

    private static List<Tranzactie> tranzactii = new ArrayList<>();

    private static Map<Integer, Integer> stocuri = new HashMap<>();

    public static void main(String[] args) {
        try {
            System.out.println("=== PORNESC PROGRAMUL DE GESTIUNE PRODUSE ===\n");

            citesteProduse();
            citesteTraanzactii();

            calculeazaStocuri();

            afiseazaNumarProduse();
            afiseazaProduseAlfabetic();
            genereazaRaport();
            afiseazaValoareStocuri();

            startServer();
        } catch (Exception e) {
            System.out.println("EROARE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void citesteProduse() throws IOException {
        System.out.println("📖 Citesc produsele din fisier...");

        BufferedReader br = new BufferedReader(new FileReader("src/produse (1).txt"));
        String linie;

        while ((linie = br.readLine()) != null) {
            String[] parti = linie.split(",");

            int cod = Integer.parseInt(parti[0]);
            String denumire = parti[1];
            double pret = Double.parseDouble(parti[2]);

            produse.add(new Produs(cod, denumire, pret));
        }

        br.close();

        System.out.println("✅ Am citit " + produse.size() + " produse");
    }

    private static void citesteTraanzactii() throws IOException {
        System.out.println("📖 Citesc tranzactiile din fisier...");

        try {
            BufferedReader br = new BufferedReader(new FileReader("src/tranzactii.json"));
            String content = br.lines().collect(Collectors.joining());
            br.close();

            content = content.replaceAll("[\\[\\]\\{\\}\\s]", "");
            String[] entries = content.split(",\"codProdus\":");

            for (String entry : entries) {
                if (entry.isEmpty()) continue;

                String[] parts = entry.split("\",\"");
                if (parts.length < 3) continue;

                try {
                    int codProdus = Integer.parseInt(parts[0].replaceAll("\"", ""));
                    int cantitate = Integer.parseInt(parts[1].split(":")[1].replaceAll("\"", ""));
                    String tip = parts[2].split(":")[1].replaceAll("\"", "");

                    tranzactii.add(new Tranzactie(codProdus, cantitate, tip));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Nu pot citi JSON-ul, folosesc date de test...");
        }

        if (tranzactii.isEmpty()) {
            System.out.println("📝 Creez tranzactii de test...");
            tranzactii.add(new Tranzactie(1, 10, "intrare"));
            tranzactii.add(new Tranzactie(1, 2, "iesire"));
            tranzactii.add(new Tranzactie(2, 5, "intrare"));
            tranzactii.add(new Tranzactie(3, 7, "intrare"));
            tranzactii.add(new Tranzactie(3, 3, "iesire"));
        }

        System.out.println("✅ Am citit " + tranzactii.size() + " tranzactii");
    }

    private static void calculeazaStocuri() {
        System.out.println("🧮 Calculez stocurile...");

        for (Tranzactie t : tranzactii) {
            int stocCurent = stocuri.getOrDefault(t.codProdus, 0);

            if (t.tip.equals("intrare")) {
                stocuri.put(t.codProdus, stocCurent + t.cantitate);
                System.out.println("  + Produs " + t.codProdus + ": +" + t.cantitate + " = " + (stocCurent + t.cantitate));
            }
            else if (t.tip.equals("iesire")) {
                stocuri.put(t.codProdus, stocCurent - t.cantitate);
                System.out.println("  - Produs " + t.codProdus + ": -" + t.cantitate + " = " + (stocCurent - t.cantitate));
            }
        }

        System.out.println("✅ Stocurile calculate!");
    }

    private static void afiseazaNumarProduse() {
        System.out.println("\n🔢 1. Numar total de produse");
        System.out.println("Numar produse: " + produse.size());
    }

    private static void afiseazaProduseAlfabetic() {
        System.out.println("\n📝 2. Produse ordonate alfabetic");

        produse.sort((p1, p2) -> p1.denumire.compareTo(p2.denumire));

        for (Produs p : produse) {
            System.out.println(p.cod + " - " + p.denumire + " - " + p.pret + " lei");
        }
    }

    private static void genereazaRaport() throws IOException {
        System.out.println("\n📊 3. Generez raportul de tranzactii");

        Map<Integer, Integer> numarTranzactii = new HashMap<>();

        for (Tranzactie t : tranzactii) {
            int numarCurent = numarTranzactii.getOrDefault(t.codProdus, 0);
            numarTranzactii.put(t.codProdus, numarCurent + 1);
        }

        List<Map.Entry<Integer, Integer>> lista = new ArrayList<>(numarTranzactii.entrySet());

        lista.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        PrintWriter pw = new PrintWriter("src/lista.txt");
        pw.println("Denumire Produs, Numar tranzactii");

        for (Map.Entry<Integer, Integer> entry : lista) {
            int codProdus = entry.getKey();
            int numar = entry.getValue();

            String denumire = "Necunoscut";
            for (Produs p : produse) {
                if (p.cod == codProdus) {
                    denumire = p.denumire;
                    break;
                }
            }

            pw.println(denumire + ", " + numar);
        }

        pw.close();

        System.out.println("✅ Raport generat in fisierul 'src/lista.txt'");
    }

    private static void afiseazaValoareStocuri() {
        System.out.println("\n💰 4. Valoarea totala a stocurilor");

        double valoareTotala = 0;

        for (Map.Entry<Integer, Integer> entry : stocuri.entrySet()) {
            int codProdus = entry.getKey();
            int cantitate = entry.getValue();

            double pret = 0.0;
            for (Produs p : produse) {
                if (p.cod == codProdus) {
                    pret = p.pret;
                    break;
                }
            }

            double valoareProdus = cantitate * pret;
            valoareTotala += valoareProdus;

            System.out.println("  Produs " + codProdus + ": " + cantitate + " x " + pret + " = " + valoareProdus + " lei");
        }

        System.out.println("✅ Valoarea totala a stocurilor: " + valoareTotala + " lei");
    }

    private static void startServer() {
        System.out.println("\n🌐 5. Pornesc serverul TCP/IP");

        Thread serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8080);
                System.out.println("🟢 Server pornit pe portul 8080, astept clienti...");

                Socket clientSocket = serverSocket.accept();
                System.out.println("👤 Client conectat!");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                String codProdusStr = in.readLine();
                int codProdus = Integer.parseInt(codProdusStr);

                System.out.println("📥 Primit de la client: cod produs " + codProdus);

                int stoc = stocuri.getOrDefault(codProdus, 0);

                out.println(stoc);

                System.out.println("📤 Trimis la client: stoc = " + stoc);

                clientSocket.close();
                serverSocket.close();
                System.out.println("🔴 Server oprit dupa servirea unei conexiuni");
            } catch (IOException e) {
                System.out.println("❌ Eroare la server: " + e.getMessage());
            }
        });

        serverThread.start();

        try {
            Thread.sleep(1000);
            startClient();
        } catch (InterruptedException e) {
            System.out.println("❌ Eroare la asteptare: " + e.getMessage());
        }
    }

    private static void startClient() {
        System.out.println("📱 Pornesc clientul de test...");

        try {
            Socket socket = new Socket("localhost", 8080);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            int codProdus = produse.isEmpty() ? 1 : produse.get(0).cod;
            out.println(codProdus);

            System.out.println("📤 Client trimite: cod produs " + codProdus);

            String raspuns = in.readLine();
            System.out.println("📥 Client primeste: stoc = " + raspuns);

            socket.close();
        } catch (IOException e) {
            System.out.println("❌ Eroare la client: " + e.getMessage());
        }
    }

    private static void creeazaFisiereTest() throws IOException {
        File dir = new File("src");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!new File("src/produse (1).txt").exists()) {
            PrintWriter pw = new PrintWriter("src/produse (1).txt");
            pw.println("1,Stafide 200g,6.03");
            pw.println("2,Seminte de pin 300g,21.18");
            pw.println("3,Nuci 500g,15.50");
            pw.println("4,Migdale 250g,18.75");
            pw.println("5,Castane 400g,12.30");
            pw.close();
        }

        if (!new File("src/tranzactii.json").exists()) {
            PrintWriter pw = new PrintWriter("src/tranzactii.json");
            pw.println("[");
            pw.println("  {\"codProdus\": 1, \"cantitate\": 10, \"tip\": \"intrare\"},");
            pw.println("  {\"codProdus\": 1, \"cantitate\": 2, \"tip\": \"iesire\"},");
            pw.println("  {\"codProdus\": 2, \"cantitate\": 5, \"tip\": \"intrare\"},");
            pw.println("  {\"codProdus\": 3, \"cantitate\": 7, \"tip\": \"intrare\"},");
            pw.println("  {\"codProdus\": 3, \"cantitate\": 3, \"tip\": \"iesire\"}");
            pw.println("]");
            pw.close();
        }
    }
}


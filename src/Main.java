import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Suporte mínimo (0-1): ");
        double minSup = Double.parseDouble(
                sc.nextLine().replace(",", ".")
        );

        System.out.print("Confiança mínima (0-1): ");
        double minConf = Double.parseDouble(
                sc.nextLine().replace(",", ".")
        );

        Apriori apriori = new Apriori(minSup, minConf);

        apriori.carregarCSV("dados.csv");

        apriori.executar();

        sc.close();
    }
}

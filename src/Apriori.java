import java.io.*;
import java.util.*;

public class Apriori {
    private List<Set<String>> transacoes = new ArrayList<>();
    private double minSup;
    private double minConf;

    public Apriori(double minSup, double minConf) {
        this.minSup = minSup;
        this.minConf = minConf;
    }

    public void carregarCSV(String caminho) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha = br.readLine();
        String[] itens = linha.split(",");
        List<String> nomesItens = new ArrayList<>();
        for (int i = 1; i < itens.length; i++) {
            nomesItens.add(itens[i].trim());
        }
        while ((linha = br.readLine()) != null) {
            String[] valores = linha.split(",");
            Set<String> transacao = new HashSet<>();
            for (int i = 1; i < valores.length; i++) {
                if (valores[i].trim().equalsIgnoreCase("sim")) {
                    transacao.add(nomesItens.get(i - 1));
                }
            }
            transacoes.add(transacao);
        }
        br.close();
    }

    public void executar() {
        Map<Set<String>, Double> itensFrequentes = new HashMap<>();
        Set<Set<String>> candidatos = gerarCandidatos();
        int k = 1;

        while (!candidatos.isEmpty()) {
            Map<Set<String>, Double> frequentes = filtrarPorSuporte(candidatos);
            itensFrequentes.putAll(frequentes);
            candidatos = gerarNovosCandidatos(frequentes.keySet(), k);
            k++;
        }
        gerarRegras(itensFrequentes);
    }

    private Set<Set<String>> gerarCandidatos() {
        Set<Set<String>> candidatos = new HashSet<>();
        for (Set<String> t : transacoes) {
            for (String item : t) {
                Set<String> conjunto = new HashSet<>();
                conjunto.add(item);
                candidatos.add(conjunto);
            }
        }
        return candidatos;
    }

    private Map<Set<String>, Double> filtrarPorSuporte(Set<Set<String>> candidatos) {
        Map<Set<String>, Double> frequentes = new HashMap<>();
        for (Set<String> candidato : candidatos) {
            int cont = 0;
            for (Set<String> t : transacoes) {
                if (t.containsAll(candidato))
                    cont++;
            }
            double suporte = (double) cont / transacoes.size();
            if (suporte >= minSup)
                frequentes.put(candidato, suporte);
        }
        return frequentes;
    }

    private Set<Set<String>> gerarNovosCandidatos(Set<Set<String>> conjuntos, int k) {
        Set<Set<String>> novos = new HashSet<>();
        List<Set<String>> lista = new ArrayList<>(conjuntos);
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                Set<String> uniao = new HashSet<>(lista.get(i));
                uniao.addAll(lista.get(j));
                if (uniao.size() == k + 1)
                    novos.add(uniao);
            }
        }
        return novos;
    }

    private void gerarRegras(Map<Set<String>, Double> itensFrequentes) {
        System.out.println("\n========== REGRAS DE ASSOCIAÇÃO ==========\n");
        int contador = 1;
        for (Set<String> conjunto : itensFrequentes.keySet()) {
            if (conjunto.size() < 2)
                continue;

            List<String> itens = new ArrayList<>(conjunto);
            for (String item : itens) {
                Set<String> antecedente = new HashSet<>(conjunto);
                antecedente.remove(item);
                Set<String> consequente = new HashSet<>();
                consequente.add(item);
                double supConjunto = itensFrequentes.get(conjunto);
                double supAntecedente = calcularSuporte(antecedente);
                double confianca = supConjunto / supAntecedente;
                if (confianca >= minConf) {
                    System.out.println("Regra " + contador++);
                    System.out.println("Antecedente : " + formatarItens(antecedente));
                    System.out.println("Consequente : " + formatarItens(consequente));
                    System.out.println("Suporte     : " + formatarPercentual(supConjunto));
                    System.out.println("Confiança   : " + formatarPercentual(confianca));
                    System.out.println("-----------------------------------------");
                }
            }
        }
    }   

    private String formatarItens(Set<String> itens) {
        return String.join(", ", itens);
    }

    private String formatarPercentual(double valor) {
        return String.format("%.0f%%", valor * 100);
    }

    private double calcularSuporte(Set<String> conjunto) {
        int cont = 0;
        for (Set<String> t : transacoes) {
            if (t.containsAll(conjunto))
                cont++;
        }
        return (double) cont / transacoes.size();
    }
}

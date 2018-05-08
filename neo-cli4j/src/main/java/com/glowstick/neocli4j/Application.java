package com.glowstick.neocli4j;

public class Application {
    public static void main(String[] args) {
        try {
//            HttpClient httpClient = HttpClients.createDefault();
//            NeoClient neoCli = new NeoClientImpl(httpClient, new URI("http://pyrpc1.neeeo.org:10332"));
//            System.out.println(neoCli.getBlockHash(2198043));
/*            Block block = neoCli.getBlock("0x0127b06ab2bad58eb4bd476d6ca3103a21a01492cf770b9cdff5e23b1a7e7c94");
            System.out.println("Block: " + block.getHash());
            for (Transaction tx : block.getTransactions()) {
                System.out.println("  Tx: " + tx.getHash());
                for (TransactionOutput txOut : tx.getOutputs()) {
                    System.out.println("    Out: " + txOut.getAddress());
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.glowstick.neocli4j;

public class Application {
    public static void main(String[] args) {
        try {
            Transaction.extractContracts("0331323303313233033132330331323303313233515501104ca552c56b6a00527ac4616168164e656f2e53746f726167652e476574436f6e746578744c154772656574696e6720746f2074686520576f726c644c0c48656c6c6f20576f726c642161615272680f4e656f2e53746f726167652e507574616168164e656f2e53746f726167652e476574436f6e746578744c154772656574696e6720746f2074686520576f726c6461617c680f4e656f2e53746f726167652e4765746c75666168134e656f2e436f6e74726163742e437265617465").forEach(System.out::println);
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

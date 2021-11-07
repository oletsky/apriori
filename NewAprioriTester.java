package mathcomp.oletsky.apriori.newapriori;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class NewAprioriTester {
    public static void main(String[] args) {

        //BasicSet
        ArrayList<Integer> items =
                formSet(1, 2, 3, 4, 5, 6, 7, 8);
        int numbItems = items.size();

        //Transactions dataset
        final String FNAME = "transact.csv";

        ArrayList<ArrayList<Integer>> transact =
                readFromCSV(FNAME);

        int numbTransact = transact.size();

        for (ArrayList<Integer> l : transact) {
            System.out.println(l);
        }

        System.out.println("Number of transactions - " + numbTransact);
        int maxLen = 0;

        for (ArrayList<Integer> l : transact) {
            if (l.size() > maxLen) maxLen = l.size();
        }
        System.out.println("Max length of transaction is " + maxLen);

        System.out.println("-------------------");

        double treshold = 0.3;

        //Important one element collections

        ArrayList<ItemSet> importantOneSets =
                new ArrayList<>();

        for (int i : items) {

            int countItem = count(i, transact);
            double freq = countItem / (numbTransact + 0.);
            if (freq > treshold) {
                //Adding frequent one-element set
                ItemSet next = new ItemSet();
                next.set = formSet(i);
                next.number = countItem;
                next.support = freq;
                importantOneSets.add(next);
            }
        }


        System.out.println("Frequent one element sets: ");

        showItemSets(importantOneSets);

        System.out.println("---------------------");
        //Forming other sets
        ArrayList<ArrayList<ItemSet>> table =
                new ArrayList<>();
        table.add(importantOneSets);
        int limLen = maxLen;
        //Main loop
        for (int len = 1; len < limLen; len++) {
            ArrayList<ItemSet> nextSet = new ArrayList<>();
            ArrayList<ItemSet> candSet = new ArrayList<>();
            for (ItemSet set1 : table.get(len - 1)) {
                //Forming candidates
                for (ItemSet set2 : importantOneSets) {
                    int itemNumb = set2.set.get(0);
                    int maxNumb=set1.set.get(set1.set.size()-1);
                    if (itemNumb>maxNumb) {
                       ItemSet cand=new ItemSet();
                       cand.set=new ArrayList<>();
                       cand.set.addAll(set1.set);
                       cand.set.add(itemNumb);

                       candSet.add(cand);

                    }
                }

            }



            //Checking candidates
            for (var c:candSet) {
                int candCount=countSet(c.set,transact);
                c.number=candCount;
                c.support=candCount/(numbTransact+0.);
                if (c.support>treshold) nextSet.add(c);
            }

            if (nextSet.isEmpty())  break;
            else table.add(nextSet);
        }

        //Forming final set of one and two elements

        ArrayList<ItemSet> finalSet = new ArrayList<>();
        for (var row:table) {
            finalSet.addAll(row);
        }


        System.out.println("Frequent sets: ");
        showItemSets(finalSet);

    }

    static void showItemSets(ArrayList<ItemSet> list) {
        if (list.size() >= 1) {
            Collections.sort(list,
                    (s1, s2) -> s2.number - s1.number);

            for (ItemSet itemSet : list) {
                System.out.println(
                        itemSet.set +
                                " - " + itemSet.number +
                                " - " +
                                String.format("%8.3f", itemSet.support)
                );
            }
        } else {
            System.out.println("No suitable itemsets");
        }
    }


    static ArrayList<Integer> formSet(int... elems) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int el : elems) {
            res.add(el);
        }
        return res;
    }

    static int count(int item, ArrayList<ArrayList<Integer>> items) {
        int cnt = 0;
        for (ArrayList<Integer> list : items) {
            if (list.contains(item)) cnt++;
        }
        return cnt;
    }

    static int countSet(ArrayList<Integer> checkList,
                        ArrayList<ArrayList<Integer>> items) {
        int cnt = 0;
        for (ArrayList<Integer> list : items) {
            if (list.containsAll(checkList)) cnt++;
        }
        return cnt;
    }

    static ArrayList<ArrayList<Integer>> readFromCSV(String fName) {
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(fName)

        )) {
            String line = null;

            while ((line = br.readLine()) != null) {
                String[] arr = line.split(";");
                ArrayList<Integer> sarr = new ArrayList<>();
                for (String s : arr) {
                    int ii = Integer.parseInt(s);
                    sarr.add(ii);
                }
                list.add(sarr);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

class ItemSet {
    ArrayList<Integer> set;
    int number;
    double support;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemSet itemSet = (ItemSet) o;

        return set.equals(itemSet.set);
    }

    @Override
    public int hashCode() {
        return set.hashCode();
    }
}



import map.*;
import unsorted.*;
import hashtable.*;
import hash.*;

void main() {

    IO.println("========================================");
    IO.println("  Map Implementations Demo");
    IO.println("========================================");
    IO.println();

    // --- UnsortedTableMap ---
    demonstrateMap(new UnsortedTableMap<>(), "UnsortedTableMap");

    // --- ChainHashMap (separate chaining) ---
    demonstrateMap(new ChainHashMap<>(), "ChainHashMap");

    // --- ProbeHashMap (linear probing) ---
    demonstrateMap(new ProbeHashMap<>(), "ProbeHashMap");

    // --- Hash function examples ---
    IO.println("========================================");
    IO.println("  Hash Function Examples");
    IO.println("========================================");
    IO.println();
    HashExamples.demo();
}

private static void demonstrateMap(Map<Integer, String> m, String name) {
    IO.println("--- " + name + " ---");
    IO.println();

    // insertions
    IO.println("put(5, A)  -> " + m.put(5, "A"));   // null (new)
    IO.println("put(7, B)  -> " + m.put(7, "B"));   // null
    IO.println("put(2, C)  -> " + m.put(2, "C"));   // null
    IO.println("put(8, D)  -> " + m.put(8, "D"));   // null
    IO.println("put(2, E)  -> " + m.put(2, "E"));   // C (replaced)
    IO.println();

    // queries
    IO.println("get(7)     -> " + m.get(7));         // B
    IO.println("get(4)     -> " + m.get(4));         // null
    IO.println("get(2)     -> " + m.get(2));         // E
    IO.println("size()     -> " + m.size());         // 4
    IO.println();

    // removal
    IO.println("remove(5)  -> " + m.remove(5));      // A
    IO.println("remove(9)  -> " + m.remove(9));      // null (absent)
    IO.println("size()     -> " + m.size());         // 3
    IO.println();

    // final state
    IO.println("entrySet() -> " + m.entrySet());
    IO.println("isEmpty()  -> " + m.isEmpty());      // false
    IO.println();
}

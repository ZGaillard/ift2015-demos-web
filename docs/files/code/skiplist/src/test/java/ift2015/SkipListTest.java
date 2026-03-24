package ift2015;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la SkipList.
 *
 * Chaque groupe de tests correspond à une méthode à implémenter.
 * Les tests sont progressifs : commencez par faire passer les tests
 * de skipSearch/get avant d'attaquer put et remove.
 *
 * Pour exécuter : mvn test
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SkipListTest {

    private SkipList<Integer, String> list;

    @BeforeEach
    void setUp() {
        list = new SkipList<>();
    }

    // =========================================================================
    // Méthodes fournies — vérification de l'état initial
    // =========================================================================

    @Test @Order(1)
    @DisplayName("Liste vide : size == 0, isEmpty == true")
    void testInitialState() {
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test @Order(2)
    @DisplayName("Liste vide : firstEntry et lastEntry retournent null")
    void testFirstLastOnEmpty() {
        assertNull(list.firstEntry());
        assertNull(list.lastEntry());
    }

    // =========================================================================
    // get — clé absente
    // =========================================================================

    @Test @Order(10)
    @DisplayName("get sur liste vide retourne null")
    void testGetOnEmpty() {
        assertNull(list.get(42));
    }

    @Test @Order(11)
    @DisplayName("get d'une clé absente retourne null")
    void testGetMissingKey() {
        list.put(10, "dix");
        list.put(30, "trente");
        assertNull(list.get(20));   // entre deux clés existantes
        assertNull(list.get(5));    // avant toutes les clés
        assertNull(list.get(50));   // après toutes les clés
    }

    // =========================================================================
    // put + get — insertion et lecture
    // =========================================================================

    @Test @Order(20)
    @DisplayName("put retourne null pour une nouvelle clé")
    void testPutNewKeyReturnsNull() {
        assertNull(list.put(1, "un"));
    }

    @Test @Order(21)
    @DisplayName("get retrouve une valeur insérée")
    void testPutThenGet() {
        list.put(5, "cinq");
        assertEquals("cinq", list.get(5));
    }

    @Test @Order(22)
    @DisplayName("get retrouve plusieurs valeurs insérées")
    void testPutMultipleKeys() {
        list.put(10, "dix");
        list.put(3,  "trois");
        list.put(7,  "sept");
        list.put(20, "vingt");
        list.put(1,  "un");

        assertEquals("dix",   list.get(10));
        assertEquals("trois", list.get(3));
        assertEquals("sept",  list.get(7));
        assertEquals("vingt", list.get(20));
        assertEquals("un",    list.get(1));
    }

    @Test @Order(23)
    @DisplayName("put met à jour la valeur si la clé existe déjà")
    void testPutUpdatesExistingKey() {
        list.put(5, "initial");
        String old = list.put(5, "mise-à-jour");

        assertEquals("initial",      old);                  // ancienne valeur retournée
        assertEquals("mise-à-jour",  list.get(5));          // nouvelle valeur en place
        assertEquals(1,              list.size());           // une seule entrée
    }

    @Test @Order(24)
    @DisplayName("put de plusieurs mises-à-jour successives sur la même clé")
    void testMultipleUpdates() {
        list.put(1, "a");
        list.put(1, "b");
        list.put(1, "c");

        assertEquals("c",  list.get(1));
        assertEquals(1,    list.size());
    }

    // =========================================================================
    // size et isEmpty
    // =========================================================================

    @Test @Order(30)
    @DisplayName("size augmente uniquement pour les nouvelles clés")
    void testSize() {
        assertEquals(0, list.size());
        list.put(1, "un");     assertEquals(1, list.size());
        list.put(2, "deux");   assertEquals(2, list.size());
        list.put(1, "ONE");    assertEquals(2, list.size()); // mise à jour, pas d'ajout
        list.put(3, "trois");  assertEquals(3, list.size());
    }

    @Test @Order(31)
    @DisplayName("isEmpty passe à false après la première insertion")
    void testIsEmpty() {
        assertTrue(list.isEmpty());
        list.put(42, "réponse");
        assertFalse(list.isEmpty());
    }

    // =========================================================================
    // remove
    // =========================================================================

    @Test @Order(40)
    @DisplayName("remove d'une clé absente retourne null")
    void testRemoveMissingKey() {
        list.put(10, "dix");
        assertNull(list.remove(99));
        assertEquals(1, list.size()); // la liste est inchangée
    }

    @Test @Order(41)
    @DisplayName("remove retourne la valeur supprimée")
    void testRemoveReturnsValue() {
        list.put(5, "cinq");
        String removed = list.remove(5);
        assertEquals("cinq", removed);
    }

    @Test @Order(42)
    @DisplayName("remove rend la clé introuvable")
    void testRemoveMakesKeyAbsent() {
        list.put(5, "cinq");
        list.remove(5);
        assertNull(list.get(5));
    }

    @Test @Order(43)
    @DisplayName("remove décrémente size")
    void testRemoveDecrementsSize() {
        list.put(1, "un");
        list.put(2, "deux");
        list.put(3, "trois");
        assertEquals(3, list.size());

        list.remove(2);
        assertEquals(2, list.size());

        list.remove(1);
        assertEquals(1, list.size());

        list.remove(3);
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test @Order(44)
    @DisplayName("remove sur liste vide retourne null")
    void testRemoveOnEmpty() {
        assertNull(list.remove(1));
    }

    @Test @Order(45)
    @DisplayName("les autres clés restent accessibles après un remove")
    void testRemoveDoesNotAffectOtherKeys() {
        list.put(10, "dix");
        list.put(20, "vingt");
        list.put(30, "trente");

        list.remove(20);

        assertEquals("dix",    list.get(10));
        assertNull(             list.get(20));
        assertEquals("trente", list.get(30));
    }

    // =========================================================================
    // firstEntry / lastEntry
    // =========================================================================

    @Test @Order(50)
    @DisplayName("firstEntry retourne la plus petite clé")
    void testFirstEntry() {
        list.put(30, "trente");
        list.put(10, "dix");
        list.put(20, "vingt");

        Entry<Integer, String> first = list.firstEntry();
        assertNotNull(first);
        assertEquals(10, first.key);
        assertEquals("dix", first.value);
    }

    @Test @Order(51)
    @DisplayName("lastEntry retourne la plus grande clé")
    void testLastEntry() {
        list.put(10, "dix");
        list.put(30, "trente");
        list.put(20, "vingt");

        Entry<Integer, String> last = list.lastEntry();
        assertNotNull(last);
        assertEquals(30, last.key);
        assertEquals("trente", last.value);
    }

    @Test @Order(52)
    @DisplayName("firstEntry et lastEntry après suppression de l'extrémité")
    void testFirstLastAfterRemoveExtreme() {
        list.put(10, "dix");
        list.put(20, "vingt");
        list.put(30, "trente");

        list.remove(10);
        assertEquals(20, list.firstEntry().key);

        list.remove(30);
        assertEquals(20, list.lastEntry().key);
    }

    // =========================================================================
    // Scénario complet
    // =========================================================================

    @Test @Order(60)
    @DisplayName("Scénario complet : insertions, mises-à-jour, suppressions")
    void testFullScenario() {
        // Insérer 10 entrées
        for (int i = 1; i <= 10; i++) {
            assertNull(list.put(i * 10, "v" + i));
        }
        assertEquals(10, list.size());

        // Vérifier toutes les valeurs
        for (int i = 1; i <= 10; i++) {
            assertEquals("v" + i, list.get(i * 10));
        }

        // Mettre à jour quelques valeurs
        assertEquals("v3", list.put(30, "trente"));
        assertEquals("v7", list.put(70, "soixante-dix"));
        assertEquals(10, list.size()); // taille inchangée

        // Vérifier les mises à jour
        assertEquals("trente",       list.get(30));
        assertEquals("soixante-dix", list.get(70));

        // Supprimer les clés paires
        for (int i = 1; i <= 10; i += 2) {
            list.remove(i * 10);
        }
        assertEquals(5, list.size());

        // Les clés impaires sont supprimées, les paires restent
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 1) assertNull(list.get(i * 10));
            else             assertNotNull(list.get(i * 10));
        }

        // Extrémités
        assertEquals(20,  list.firstEntry().key);
        assertEquals(100, list.lastEntry().key);
    }
}

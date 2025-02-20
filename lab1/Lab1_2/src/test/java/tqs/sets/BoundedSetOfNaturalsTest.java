/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.sets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.sets.BoundedSetOfNaturals;

/**
 * @author ico0
 */
class BoundedSetOfNaturalsTest {
    private BoundedSetOfNaturals setA;
    private BoundedSetOfNaturals setB;
    private BoundedSetOfNaturals setC;
    private BoundedSetOfNaturals set;


    @BeforeEach
    public void setUp() {
        setA = new BoundedSetOfNaturals(1);
        setB = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30, 40, 50, 60});
        setC = BoundedSetOfNaturals.fromArray(new int[]{50, 60});
        set = new BoundedSetOfNaturals(5);
    }

    @AfterEach
    public void tearDown() {
        setA = setB = setC = null;
    }

    @Test
    public void testAddElementToEmptySet() {
        setA.add(9);
        assertTrue(setA.contains(9), "The element should be added to the empty set.");
        assertEquals(1, setA.size(), "Size should be 1 after adding an element.");
    }

    @Test
    public void testAddElement() {

        setA.add(9);
        assertTrue(setA.contains(9), "add: added element not found in set.");
        assertEquals(1, setA.size());

    }

    public void testAddValidElements() {
        set.add(10);
        assertTrue(set.contains(10), "add: added element should be present in the set");
        assertEquals(1, set.size(), "add: size should be updated after adding an element");
    }

    @Test
    public void testAddFromBadArray() {
        int[] elems = new int[]{10, -20, -30};

        // must fail with exception
        assertThrows(IllegalArgumentException.class, () -> setA.add(elems));
    }

    @Test
    void testAddIllegalNumber() {
        assertThrows(IllegalArgumentException.class, () -> setA.add(-1),
                "add: negative number was added to set.");
    }

    @Test
    void testAddDuplicateInput() {
        setA.add(10);
        assertThrows(IllegalArgumentException.class, () -> setA.add(10),
                "add: element was added when it was already in the set.");
    }

    @Test
    public void testAddNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> setA.add(-5),
                "add: negative values should not be allowed.");
    }

    @Test
    public void testAddZero() {
        assertThrows(IllegalArgumentException.class, () -> setA.add(0),
                "add: zero should not be allowed.");
    }

    @Test
    void testEqualsNull() {
        assertFalse(setA.equals(null), "equals: setA is not null");
    }

    @Test
    void testEqualsDifferentClass() {
        assertFalse(setA.equals(new Object()), "equals: setA is not an Object");
    }

    @Test
    void testHashCodeConsistency() {
        int hashCode1 = setA.hashCode();
        int hashCode2 = setA.hashCode();
        assertEquals(hashCode1, hashCode2, "hashCode: same object must have same hash code");
    }

    @Test
    void testHashCodeInequality() {
        assertNotEquals(setA.hashCode(), setB.hashCode(),
                "hashCode: different objects must have different hash codes");
    }

    @Test
    void testAddBeyondCapacity() {
        BoundedSetOfNaturals limitedSet = new BoundedSetOfNaturals(2);
        limitedSet.add(1);
        limitedSet.add(2);
        assertThrows(IllegalArgumentException.class, () -> limitedSet.add(3),
                "Should not allow adding elements beyond capacity");
    }

    @Test
    void testEqualsWithSameElements() {
        BoundedSetOfNaturals set1 = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        BoundedSetOfNaturals set2 = BoundedSetOfNaturals.fromArray(new int[]{1, 2, 3});
        assertEquals(set1, set2, "Sets with same elements should be equal");
    }

    @Test
    void testContainsWithNonExistentElement() {
        assertFalse(setB.contains(100), "Set should not contain elements that weren't added");
    }


    @Test
    void testFromArrayWithSingleElement() {
        BoundedSetOfNaturals singleElementSet = BoundedSetOfNaturals.fromArray(new int[]{42});
        assertEquals(1, singleElementSet.size(), "Set should have exactly one element");
        assertTrue(singleElementSet.contains(42), "Set should contain the added element");
    }

    @Test
    void testEqualsSameInstance() {
        BoundedSetOfNaturals instance = new BoundedSetOfNaturals(5);
        instance.add(1);
        assertTrue(instance.equals(instance), "Set should be equal to itself");
    }
    
    @Test
    public void testIntersectsWithNoCommonElements() {
        BoundedSetOfNaturals set1 = new BoundedSetOfNaturals(5);
        set1.add(1);
        set1.add(2);
        set1.add(3);

        BoundedSetOfNaturals set2 = new BoundedSetOfNaturals(5);
        set2.add(4);
        set2.add(5);
        set2.add(6);

        assertFalse(set1.intersects(set2), "intersects: sets should not intersect when there are no common elements.");
    }
}
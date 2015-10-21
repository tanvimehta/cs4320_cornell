import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class BCNFTest {

  /**
   * Performs a basic test on a simple table.
   * gives input attributes (a,b,c) and functional dependency a->c
   * and expects output (a,c),(b,c) or any reordering
   **/
  @Test
  public void testSimpleBCNF() {
    //construct table
    AttributeSet attrs = new AttributeSet();
    attrs.addAttribute(new Attribute("a"));
    attrs.addAttribute(new Attribute("b"));
    attrs.addAttribute(new Attribute("c"));

    //create functional dependencies
    Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
    AttributeSet ind = new AttributeSet();
    AttributeSet dep = new AttributeSet();
    ind.addAttribute(new Attribute("a"));
    dep.addAttribute(new Attribute("c"));
    FunctionalDependency fd = new FunctionalDependency(ind, dep);
    fds.add(fd);

    //run client code
    Set<AttributeSet> bcnf = BCNF.decompose(attrs, fds);

    //verify output
    assertEquals("Incorrect number of tables", 2, bcnf.size());

    for(AttributeSet as : bcnf) {
      assertEquals("Incorrect number of attributes", 2, as.size());
      assertTrue("Incorrect table", as.contains(new Attribute("a")));
    }
  }
  
  @Test
  public void testBCNFQuestion3() {
    //construct table
    AttributeSet attrs = new AttributeSet();
    attrs.addAttribute(new Attribute("a"));
    attrs.addAttribute(new Attribute("b"));
    attrs.addAttribute(new Attribute("c"));
    attrs.addAttribute(new Attribute("d"));
    attrs.addAttribute(new Attribute("e"));
    attrs.addAttribute(new Attribute("f"));
    attrs.addAttribute(new Attribute("g"));

    //create functional dependencies
    // D -> BC
    Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
    AttributeSet ind = new AttributeSet();
    AttributeSet dep = new AttributeSet();
    ind.addAttribute(new Attribute("d"));
    dep.addAttribute(new Attribute("b"));
    dep.addAttribute(new Attribute("c"));
    FunctionalDependency fd = new FunctionalDependency(ind, dep);
    fds.add(fd);
    
    // AF -> E
    ind = new AttributeSet();
    dep = new AttributeSet();
    ind.addAttribute(new Attribute("a"));
    ind.addAttribute(new Attribute("f"));
    dep.addAttribute(new Attribute("e"));
    fd = new FunctionalDependency(ind, dep);
    fds.add(fd);
    
    // B -> AC
    ind = new AttributeSet();
    dep = new AttributeSet();
    ind.addAttribute(new Attribute("b"));
    dep.addAttribute(new Attribute("a"));
    dep.addAttribute(new Attribute("c"));
    fd = new FunctionalDependency(ind, dep);
    fds.add(fd);

    //run client code
    Set<AttributeSet> bcnf = BCNF.decompose(attrs, fds);

    //verify output
    assertEquals("Incorrect number of tables", 2, bcnf.size());

    for(AttributeSet as : bcnf) {
      assertEquals("Incorrect number of attributes", 2, as.size());
      assertTrue("Incorrect table", as.contains(new Attribute("a")));
    }
  }
  
  @Test
  public void testBCNFQuestion19_5_1() {
    //construct table
    AttributeSet attrs = new AttributeSet();
    attrs.addAttribute(new Attribute("a"));
    attrs.addAttribute(new Attribute("b"));
    attrs.addAttribute(new Attribute("c"));
    attrs.addAttribute(new Attribute("d"));
    attrs.addAttribute(new Attribute("e"));

    //create functional dependencies
    // A -> B
    Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
    AttributeSet ind = new AttributeSet();
    AttributeSet dep = new AttributeSet();
    ind.addAttribute(new Attribute("a"));
    dep.addAttribute(new Attribute("b"));
    FunctionalDependency fd = new FunctionalDependency(ind, dep);
    fds.add(fd);
    
    // C -> D
    ind = new AttributeSet();
    dep = new AttributeSet();
    ind.addAttribute(new Attribute("c"));
    dep.addAttribute(new Attribute("d"));
    fd = new FunctionalDependency(ind, dep);
    fds.add(fd);

    //run client code
    Set<AttributeSet> bcnf = BCNF.decompose(attrs, fds);

    //verify output
    assertEquals("Incorrect number of tables", 3, bcnf.size());

//    for(AttributeSet as : bcnf) {
//      assertEquals("Incorrect number of attributes", 3, as.size());
//      assertTrue("Incorrect table", as.contains(new Attribute("a")));
//    }
  }
  
  @Test
  public void testBCNFQuestion19_5_2() {
    //construct table
    AttributeSet attrs = new AttributeSet();
    attrs.addAttribute(new Attribute("a"));
    attrs.addAttribute(new Attribute("b"));
    attrs.addAttribute(new Attribute("f"));

    //create functional dependencies
    // A -> B
    Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
    AttributeSet ind = new AttributeSet();
    AttributeSet dep = new AttributeSet();
    ind.addAttribute(new Attribute("a"));
    ind.addAttribute(new Attribute("c"));
    dep.addAttribute(new Attribute("e"));
    FunctionalDependency fd = new FunctionalDependency(ind, dep);
    fds.add(fd);
    
    // C -> D
    ind = new AttributeSet();
    dep = new AttributeSet();
    ind.addAttribute(new Attribute("b"));
    dep.addAttribute(new Attribute("f"));
    fd = new FunctionalDependency(ind, dep);
    fds.add(fd);

    //run client code
    Set<AttributeSet> bcnf = BCNF.decompose(attrs, fds);

    //verify output
    assertEquals("Incorrect number of tables", 2, bcnf.size());

    for(AttributeSet as : bcnf) {
      assertEquals("Incorrect number of attributes", 2, as.size());
      assertTrue("Incorrect table", as.contains(new Attribute("b")));
    }
  }
}
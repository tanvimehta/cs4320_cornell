import java.util.*;

public class BCNF {

  /**
   * Decomposition using BCNF algorithm 
   **/
  public static Set<AttributeSet> decompose(AttributeSet attributeSet,
                                            Set<FunctionalDependency> functionalDependencies) {
	  
	  Set<AttributeSet> bcnf = new HashSet<AttributeSet>();
	  Set<AttributeSet> powerSet = getAllSubsets(attributeSet, 0);
	  
	  for (AttributeSet subset: powerSet) {
		AttributeSet closure = closure(subset, functionalDependencies);
		
		// Hint: Make sure to consider the case where the set of functional dependencies
		// include attributes not in the relation
		AttributeSet cleanClosure = new AttributeSet();
		for (Attribute attr: closure.get_attributes()) {
			if (attributeSet.contains(attr)) {
				cleanClosure.addAttribute(attr);
			}
		}
		
		// If X is a key or determines only itself, try a different set of attributes
		if (cleanClosure.equals(subset) || cleanClosure.equals(attributeSet)) {
			continue;
		}
		
		// Separate the table into X+ and X U (X+)compliment
		List<Attribute> xClosureComplimentUnionX = new ArrayList<Attribute>(subset.get_attributes());
		for (Attribute attr: attributeSet.get_attributes()) {
			if (!cleanClosure.contains(attr)) {
				xClosureComplimentUnionX.add(attr);
			}
		}
		
		AttributeSet bcnfSet2 = new AttributeSet(xClosureComplimentUnionX);
		
		// Recurse on each side
		bcnf.addAll(decompose(cleanClosure, functionalDependencies));
		bcnf.addAll(decompose(bcnfSet2, functionalDependencies));
		return bcnf;	
	  }
	  
	  bcnf.add(attributeSet);
	  return bcnf;
  }

  /**
   * Closure implementation as per MAIER algorithm 4.4
   **/
  public static AttributeSet closure(AttributeSet attributeSet, Set<FunctionalDependency> functionalDependencies) {
	  
	  // LIST
	  Map<Attribute,Set<FunctionalDependency>> listMap = new HashMap<Attribute,Set<FunctionalDependency>>();
	  
	  // COUNT
	  Map<FunctionalDependency, Integer> countMap = new HashMap<FunctionalDependency, Integer>();
	  
	  // Initialization
	  for (FunctionalDependency fd: functionalDependencies) {
		  countMap.put(fd, fd.independent().size());
		  Iterator<Attribute> fdAttrIter = fd.independent().iterator();
		  
		  while(fdAttrIter.hasNext()) {
			  Attribute left = fdAttrIter.next();
			  
			  if(!listMap.containsKey(left)) {
				  Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
				  fds.add(fd);
				  
				  listMap.put(left, fds);
			  } else {
				  listMap.get(left).add(fd);
			  }
			  
		  }
	  }
	  
	  // NEWDEP
	  AttributeSet newDep = new AttributeSet(attributeSet);
	  // UPDATE
	  AttributeSet update = new AttributeSet(attributeSet);
	  
	  // Computation
	  while(update.size() != 0) {
		  Attribute a = update.get_attributes().remove(0);
		  
		  if(listMap.containsKey(a)) {
			  for (FunctionalDependency fd: listMap.get(a)) {
				  countMap.put(fd, countMap.get(fd)-1);
				  
				  if (countMap.get(fd) == 0) {
					  AttributeSet z = fd.dependent();
					  AttributeSet add = new AttributeSet();
					  
					  // ADD := Z = NEWDEP
					  for (Attribute attr: z.get_attributes()) {
						 if (!newDep.contains(attr)) {
							 add.addAttribute(attr);
						 }
					  }
					  
					  // NEWDEP := NEWDEP U ADD
					  // UPDATE := NEWDEP U ADD
					  for (Attribute attr : add.get_attributes()) {
						  newDep.addAttribute(attr);
						  update.addAttribute(attr);
					  }
				  }
			  }
		  }
	  }
	  return newDep;
  }
  
  /**
   * Computes the power set of the attribute set.
   * Used treeset http://stackoverflow.com/questions/3380312/ordering-a-hashset-example
   * @param attrSet attribute set
   * @param index of the current attribute
   * @return all possible subsets of the attribute set
   */
  public static Set<AttributeSet> getAllSubsets (AttributeSet attrSet, int index) {
	  Set<AttributeSet> powerSet;
	  List<Attribute> set = attrSet.get_attributes();
	  if (set.size() == index) {
		  powerSet = new TreeSet<AttributeSet>(new Comparator<AttributeSet>() {
	            @Override
	            public int compare(AttributeSet o1, AttributeSet o2) {
	                if(o1.size() > o2.size()) {
	                    return 1;
	                } else {
	                    return -1;
	                }
	            }
	        });
		  powerSet.add(new AttributeSet());
	  } else {
		  powerSet = getAllSubsets(attrSet, index + 1);
		  Attribute item = attrSet.get_attributes().get(index);
		  Set<AttributeSet> moreSubsets = new HashSet<AttributeSet>();
		  for (AttributeSet subset: powerSet) {
			  AttributeSet newSubset = new AttributeSet();
			  newSubset.bulkAddAttribute(subset);
			  newSubset.addAttribute(item);
			  moreSubsets.add(newSubset);
		  }
		  powerSet.addAll(moreSubsets);
	  }
	  return powerSet;  
  }
}

package net.hostettler.jdd.dd.sdd;

import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.ddd.DDDUp;
import net.hostettler.jdd.dd.sdd.SDDHomImpl;
import net.hostettler.jdd.dd.sdd.SDDIdHom;
import net.hostettler.jdd.dd.sdd.SDDImpl;
import net.hostettler.jdd.dd.sdd.SDDLocalHom;
import net.hostettler.jdd.dd.sdd.SDDRelocationHom;
import net.hostettler.jdd.dd.sdd.SDDUp;
import net.hostettler.jdd.dd.sdd.SimplePropagationSDDHomImpl;

public class SDDHomTest {
	private Hom<String, ValSet<Integer>> mUp;

	private Hom<String, ValSet<Integer>> mDown;

	private Hom<String, ValSet<Integer>> mRename;

	private Hom<String, ValSet<Integer>> mSwap;

	@Before
	public void setup() {
		this.mRename = new SDDHomImpl<String, Integer>() {

			protected DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				return SDDImpl.create((String) param1VarArgs[0], (ValSet) param1ValSet, id(param1Map, param1ValSet));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.SDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 4877;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		this.mUp = new SDDHomImpl<String, Integer>() {
			protected DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				String str = (String) param1VarArgs[0];
				ValSet<Integer> valSet = (ValSet) param1VarArgs[1];
				return SDDImpl.create(param1String, param1ValSet,
						SDDImpl.create(str, valSet, id(param1Map, param1ValSet)));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.SDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 6373;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		this.mDown = new SDDHomImpl<String, Integer>() {
			protected DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				String str = (String) param1VarArgs[0];
				ValSet<Integer> valSet = (ValSet) param1VarArgs[1];
				return (param1String.equals(str)
						? SDDImpl.create(param1String, param1ValSet,
								SDDImpl.create(str, valSet, id(param1Map, param1ValSet)))
						: SDDHomTest.this.mUp.phi(phi(id(param1Map, param1ValSet), new Object[] { str, valSet }),
								new Object[] { param1String, param1ValSet }));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.SDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 271;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		this.mSwap = new SDDHomImpl<String, Integer>() {
			protected DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				String str1 = (String) param1VarArgs[0];
				String str2 = (String) param1VarArgs[1];
				return (param1String.equals(str1)
						? SDDHomTest.this.mRename.phi(SDDHomTest.this.mDown.phi(id(param1Map, param1ValSet),
								new Object[] { str2, param1ValSet }), new Object[] { str1 })
						: (param1String.equals(str2)
								? SDDHomTest.this.mRename.phi(SDDHomTest.this.mDown.phi(id(param1Map, param1ValSet),
										new Object[] { str1, param1ValSet }), new Object[] { str2 })
								: SDDImpl.create(param1String, param1ValSet,
										phi(id(param1Map, param1ValSet), param1VarArgs))));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.SDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 2371;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
	}

	@Test
	public void testSimpleHomomorphism() {
		Hom<String, ValSet<Integer>> sDDHomImpl = new SDDHomImpl<String, Integer>() {

			private ValSet<Integer> inc(ValSet<Integer> param1ValSet) {
				HashSet<Integer> hashSet = new HashSet();
				for (Integer integer : param1ValSet)
					hashSet.add(Integer.valueOf(integer.intValue() + 1));
				return (ValSet<Integer>) ObjSet.create(hashSet);
			}

			public DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				return param1String.equals(param1VarArgs[0])
						? SDDImpl.create(param1String, inc(param1ValSet), id(param1Map, param1ValSet))
						: SDDImpl.create(param1String, param1ValSet, phi(param1Map.get(param1ValSet), param1VarArgs));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.SDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 1453;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("a", ObjSet.create(Integer.valueOf(3))));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(5)));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(6))));
		sDD2 = sDD2.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(7))));
		SDDImpl.create("e", ObjSet.create(Integer.valueOf(7)));
		sDD1 = sDD1.union(sDD2);
		System.out.println(sDD1);
		sDD1 = sDDHomImpl.phi(sDD1, new Object[] { "a" });
		System.out.println("Inc : " + sDD1);
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(2)));
		sDD3 = sDD3.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD3 = sDD3.append(SDDImpl.create("a", ObjSet.create(Integer.valueOf(3))));
		DD<String, ValSet<Integer>> sDD4 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(6)));
		sDD4 = sDD4.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(6))));
		sDD4 = sDD4.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(7))));
		sDD3 = sDD3.union(sDD4);
		SDDImpl.create("b", ObjSet.create(Integer.valueOf(2)));
		Assert.assertSame(sDD1, sDD3);
	}

	@Test
	public void testRenameHomomorphism() {
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		System.out.println("d : " + sDD1);
		sDD1 = this.mRename.phi(sDD1, new Object[] { "c" });
		System.out.println("rename(d, c) " + sDD1);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("c", ObjSet.create(Integer.valueOf(1)));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		Assert.assertSame(sDD2, sDD1);
	}

	@Test
	public void testUpHomomorphism() {
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		System.out.println("d : " + sDD1);
		sDD1 = this.mUp.phi(sDD1, new Object[] { "c", ObjSet.create(Integer.valueOf(3)) });
		System.out.println("up(d, c, {3}) " + sDD1);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		Assert.assertSame(sDD2, sDD1);
	}

	@Test
	public void testDownHomomorphism() {
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		System.out.println("d : " + sDD1);
		sDD1 = this.mDown.phi(sDD1, new Object[] { "c", ObjSet.create(Integer.valueOf(8)) });
		System.out.println("down(d, c, {8}) " + sDD1);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("c", ObjSet.create(Integer.valueOf(3)));
		sDD2 = sDD2.append(SDDImpl.create("a", ObjSet.create(Integer.valueOf(1))));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(8))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		Assert.assertSame(sDD2, sDD1);
	}

	@Test
	public void testSwapHomomorphism() {
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(4))));
		System.out.println("d : " + sDD1);
		sDD1 = this.mSwap.phi(sDD1, new Object[] { "b", "d" });
		System.out.println("swap(d, 'b', 'd') " + sDD1);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(4))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(2))));
		Assert.assertSame(sDD2, sDD1);
	}

	@Test
	public void testComposeHommomorphism() {
		DD<String, ValSet<Integer>> sDD = SDDImpl.create("b", ObjSet.create(Integer.valueOf(8)));
		SDDHomImpl<String, Integer> sDDHomImpl1 = new SDDHomImpl<String, Integer>() {
			protected DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				return SDDImpl.create(param1String, param1ValSet,
						SDDImpl.create("H", (ValSet<Integer>) ObjSet.create(Integer.valueOf(1)),
								phi(id(param1Map, param1ValSet), new Object[0])));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.create("T", ObjSet.create(Integer.valueOf(1)));
			}

			public int computeHashCode() {
				return getClass().hashCode() * 1423;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		SDDHomImpl<String, Integer> sDDHomImpl2 = new SDDHomImpl<String, Integer>() {
			protected DD<String, ValSet<Integer>> phi(String param1String, ValSet<Integer> param1ValSet,
					Map<ValSet<Integer>, DD<String, ValSet<Integer>>> param1Map, Object... param1VarArgs) {
				return SDDImpl.create(param1String, param1ValSet, SDDImpl.create("H", ObjSet.create(Integer.valueOf(2)),
						phi(id(param1Map, param1ValSet), new Object[0])));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return SDDImpl.create("T", ObjSet.create(Integer.valueOf(2)));
			}

			public int computeHashCode() {
				return getClass().hashCode() * 2621;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		Object object = ((Hom) sDDHomImpl1.compose(sDDHomImpl2)).phi(sDD, new Object[0]);
		System.out.println(object);
		Assert.assertEquals(
				SDDImpl.create("b", ObjSet.create(Integer.valueOf(8)), SDDImpl.create("H",
						ObjSet.create(Integer.valueOf(2)),
						SDDImpl.create("H", ObjSet.create(Integer.valueOf(1)),
								SDDImpl.create("H", ObjSet.create(Integer.valueOf(2)),
										SDDImpl.create("T", ObjSet.create(Integer.valueOf(1)),
												SDDImpl.create("H", ObjSet.create(Integer.valueOf(2)),
														SDDImpl.create("T", ObjSet.create(Integer.valueOf(2))))))))),
				object);
	}

	@Test
	public void testRelocationEq() {
		System.out.println("****** Reloc ********");
		SDDRelocationHom<String, Object> sDDRelocationHom1 = new SDDRelocationHom<String, Object>("d");
		SDDRelocationHom<String, Object> sDDRelocationHom2 = new SDDRelocationHom<String, Object>("d");
		Assert.assertEquals(sDDRelocationHom1, sDDRelocationHom2);
	}

	@Test
	public void testRelocation() {
		System.out.println("****** Reloc ********");
		SDDRelocationHom<String, Integer> sDDRelocationHom = new SDDRelocationHom<String, Integer>("d");
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(5)));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(6))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(7))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.union(sDD2);
		System.out.println(sDD1);
		Object object = sDDRelocationHom.phi(sDD1, new Object[0]);
		System.out.println("Reloc : " + object);
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("d", ObjSet.create(Integer.valueOf(3)));
		sDD3 = sDD3.append(SDDImpl.create("a", ObjSet.create(Integer.valueOf(1))));
		sDD3 = sDD3.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD3 = sDD3.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD3 = sDD3.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		DD<String, ValSet<Integer>> sDD4 = SDDImpl.create("d", ObjSet.create(Integer.valueOf(3)));
		sDD4 = sDD4.append(SDDImpl.create("a", ObjSet.create(Integer.valueOf(5))));
		sDD4 = sDD4.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(6))));
		sDD4 = sDD4.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(7))));
		sDD4 = sDD4.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		sDD3 = sDD3.union(sDD4);
		Assert.assertSame(object, sDD3);
	}

	@Test
	public void testRelocPhi1() {
		System.out.println("****** Reloc ********");
		SDDRelocationHom<String, Object> sDDRelocationHom = new SDDRelocationHom<String, Object>("d");
		DD<?, ?> sDD = sDDRelocationHom.phi1(new Object[] { SDDImpl.SDD_TRUE });
		Assert.assertSame(sDD, SDDImpl.SDD_ANY);
	}

	@Test
	public void testUp() {
		System.out.println("****** Up ********");
		SDDUp<String, Integer> sDDUp = new SDDUp<String, Integer>("d", ObjSet.create(Integer.valueOf(10)));
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		System.out.println(sDD1);
		Object object = sDDUp.phi(sDD1, new Object[0]);
		System.out.println("Up : " + object);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(10))));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		Assert.assertSame(object, sDD2);
	}

	@Test
	public void testUpPhi1() {
		System.out.println("****** Up ********");
		SDDUp<String, Object> sDDUp = new SDDUp<String, Object>("d", ObjSet.create(Integer.valueOf(10)));
		DD<?, ?> sDD = sDDUp.phi1(new Object[] { SDDImpl.SDD_TRUE });
		Assert.assertSame(sDD, SDDImpl.SDD_ANY);
	}

	@Test
	public void testIdHom() {
		System.out.println("****** Id ********");
		SDDIdHom<String, Integer> sDDIdHom = new SDDIdHom<String, Integer>();
		DD<String, ValSet<Integer>> sDD = SDDImpl.create("a", ObjSet.create(Integer.valueOf(1)));
		sDD = sDD.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD = sDD.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD = sDD.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD = sDD.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		Assert.assertSame(sDDIdHom.phi(sDD, new Object[0]), sDD);
		Assert.assertEquals(new SDDIdHom<Object, Object>(), new SDDIdHom<Object, Object>());
	}

	@Test
	public void testLocalHom() {
		System.out.println("****** Local ********");
		SDDLocalHom<String, Integer, String, Integer> sDDLocalHom = new SDDLocalHom<String, Integer, String, Integer>(
				new DDDUp("a", Integer.valueOf(1)), "a");
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", DDDImpl.create("b", Integer.valueOf(1)));
		sDD1 = sDD1.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD1 = sDD1.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD1 = sDD1.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a",
				DDDImpl.create("b", Integer.valueOf(1), DDDImpl.create("a", Integer.valueOf(1))));
		sDD2 = sDD2.append(SDDImpl.create("b", ObjSet.create(Integer.valueOf(2))));
		sDD2 = sDD2.append(SDDImpl.create("c", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("d", ObjSet.create(Integer.valueOf(3))));
		sDD2 = sDD2.append(SDDImpl.create("e", ObjSet.create(Integer.valueOf(3))));
		Assert.assertSame(sDD2, sDDLocalHom.phi(sDD1, new Object[0]));
		Assert.assertEquals(new SDDLocalHom<String, Object, Object, Object>(new DDDUp("a", Integer.valueOf(1)), "a"),
				new SDDLocalHom<String, Object, Object, Object>(new DDDUp("a", Integer.valueOf(1)), "a"));
	}

	@Test
	public void testMyFilterHom() {
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a",  ObjSet.create(1,2,3), SDDImpl.create("b",  ObjSet.create(4,2,3)));


		Hom<String, ValSet<Integer>> filter = new SimplePropagationSDDHomImpl<String, Integer>() {

				@Override
				protected DD<String, ValSet<Integer>> phi(String var, ValSet<Integer> values,
						Map<ValSet<Integer>, DD<String, ValSet<Integer>>> alpha, Object... parameters) {
					String str = (String) parameters[0];
					ValSet<Integer> valuesToFilterOut = (ValSet<Integer> ) parameters[1];
					
					return DDDImpl.create(var, values.difference(valuesToFilterOut), phi(id(alpha, values), parameters));	
				}
		};
				
		System.out.println(filter.phi(sDD1, new Object[] { "c", ObjSet.create(2,3) }));
	}
}

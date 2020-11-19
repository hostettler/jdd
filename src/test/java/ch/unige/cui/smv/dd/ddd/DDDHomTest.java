package ch.unige.cui.smv.dd.ddd;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;

public class DDDHomTest {
	private Hom<String, Integer> mUp;

	private Hom<String, Integer> mDown;

	private Hom<String, Integer> mRename;

	private Hom<String, Integer> mSwap;

	@Before
	public void setup() {
		this.mRename = new DDDHomImpl<String, Integer>() {
			protected DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				return DDDImpl.create((String) param1VarArgs[0], param1Integer,
						(DD<String, Integer>) id(param1Map, param1Integer));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return DDDImpl.DDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 7523;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		this.mUp = new DDDHomImpl<String, Integer>() {
			protected DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				String str = (String) param1VarArgs[0];
				Integer integer = (Integer) param1VarArgs[1];
				return DDDImpl.create(param1String, param1Integer,
						DDDImpl.create(str, integer, id(param1Map, param1Integer)));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return DDDImpl.DDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 4289;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		this.mDown = new DDDHomImpl<String, Integer>() {
			protected DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				String str = (String) param1VarArgs[0];
				Integer integer = (Integer) param1VarArgs[1];
				return (DD<String, Integer>) (param1String.equals(str)
						? DDDImpl.create(param1String, param1Integer,
								DDDImpl.create(str, integer, id(param1Map, param1Integer)))
						: DDDHomTest.this.mUp.phi(phi(id(param1Map, param1Integer), new Object[] { str, integer }),
								new Object[] { param1String, param1Integer }));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return DDDImpl.DDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 6199;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		this.mSwap = new DDDHomImpl<String, Integer>() {
			protected DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				String str1 = (String) param1VarArgs[0];
				String str2 = (String) param1VarArgs[1];
				return (DD<String, Integer>) (param1String.equals(str1)
						? DDDHomTest.this.mRename.phi(DDDHomTest.this.mDown.phi(id(param1Map, param1Integer),
								new Object[] { str2, param1Integer }), new Object[] { str1 })
						: (param1String.equals(str2)
								? DDDHomTest.this.mRename.phi(DDDHomTest.this.mDown.phi(id(param1Map, param1Integer),
										new Object[] { str1, param1Integer }), new Object[] { str2 })
								: DDDImpl.create(param1String, param1Integer,
										phi(id(param1Map, param1Integer), param1VarArgs))));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return DDDImpl.DDD_ANY;
			}

			public int computeHashCode() {
				return getClass().hashCode() * 3659;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
	}

	@Test
	public void testSimpleHomomorphism() {
		DDDHomImpl<String, Integer> dDDHomImpl = new DDDHomImpl<String, Integer>() {
			public DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				return param1String.equals(param1VarArgs[0])
						? DDDImpl.create(param1String, Integer.valueOf(param1Integer.intValue() + 1),
								(DD<String, Integer>) id(param1Map, param1Integer))
						: DDDImpl.create(param1String, param1Integer,
								(DD<String, Integer>) phi(param1Map.get(param1Integer), param1VarArgs));
			}

			protected DD<String, Integer> phi1(Object... param1VarArgs) {
				return getDDAny();
			}

			public int computeHashCode() {
				return getClass().hashCode() * 3557;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("a", Integer.valueOf(3)));
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(5));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("d", Integer.valueOf(6)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("e", Integer.valueOf(7)));
		DDDImpl.create("e", Integer.valueOf(7));
		dDD1 = (DD<String, Integer>) dDD1.union(dDD2);
		System.out.println(dDD1);
		dDD1 = dDDHomImpl.phi(dDD1, new Object[] { "a" });
		System.out.println("Inc : " + dDD1);
		DD<String, Integer> dDD3 = DDDImpl.create("a", Integer.valueOf(2));
		dDD3 = (DD<String, Integer>) dDD3.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD3 = (DD<String, Integer>) dDD3.append(DDDImpl.create("a", Integer.valueOf(3)));
		DD<String, Integer> dDD4 = DDDImpl.create("a", Integer.valueOf(6));
		dDD4 = (DD<String, Integer>) dDD4.append(DDDImpl.create("d", Integer.valueOf(6)));
		dDD4 = (DD<String, Integer>) dDD4.append(DDDImpl.create("e", Integer.valueOf(7)));
		dDD3 = (DD<String, Integer>) dDD3.union(dDD4);
		DDDImpl.create("b", Integer.valueOf(2));
		Assert.assertSame(dDD1, dDD3);
	}

	@Test
	public void testRenameHomomorphism() {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("d", Integer.valueOf(4)));
		System.out.println("d : " + dDD1);
		dDD1 = this.mRename.phi(dDD1, new Object[] { "c" });
		System.out.println("rename(d, c) " + dDD1);
		DD<String, Integer> dDD2 = DDDImpl.create("c", Integer.valueOf(1));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("d", Integer.valueOf(4)));
		Assert.assertSame(dDD2, dDD1);
	}

	@Test
	public void testUpHomomorphism() {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("d", Integer.valueOf(4)));
		System.out.println("d : " + dDD1);
		dDD1 = this.mUp.phi(dDD1, new Object[] { "c", Integer.valueOf(3) });
		System.out.println("up(d, c, {3}) " + dDD1);
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(1));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("d", Integer.valueOf(4)));
		Assert.assertSame(dDD2, dDD1);
	}

	@Test
	public void testDownHomomorphism() {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("d", Integer.valueOf(4)));
		System.out.println("d : " + dDD1);
		dDD1 = this.mDown.phi(dDD1, new Object[] { "c", Integer.valueOf(8) });
		System.out.println("down(d, c, {8}) " + dDD1);
		DD<String, Integer> dDD2 = DDDImpl.create("c", Integer.valueOf(3));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("a", Integer.valueOf(1)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("c", Integer.valueOf(8)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("d", Integer.valueOf(4)));
		Assert.assertSame(dDD2, dDD1);
	}

	@Test
	public void testSwapHomomorphism() {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("d", Integer.valueOf(4)));
		System.out.println("d : " + dDD1);
		dDD1 = this.mSwap.phi(dDD1, new Object[] { "b", "d" });
		System.out.println("swap(d, 'b', 'd') " + dDD1);
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(1));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("b", Integer.valueOf(4)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("d", Integer.valueOf(2)));
		Assert.assertSame(dDD2, dDD1);
	}

	@Test
	public void testComposeHommomorphism() {
		DD<String, Integer> dDD = DDDImpl.create("b", Integer.valueOf(8));
		
		DDDHomImpl<String, Integer> dDDHomImpl1 = new DDDHomImpl<>() {
			protected DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				return DDDImpl.create(param1String, param1Integer,
						DDDImpl.create("H", Integer.valueOf(1), phi(id(param1Map, param1Integer), new Object[0])));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return DDDImpl.create("T", Integer.valueOf(1));
			}

			public int computeHashCode() {
				return getClass().hashCode() * 3847;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		
		DDDHomImpl<String, Integer> dDDHomImpl2 = new DDDHomImpl<String, Integer>() {
			
			protected DD<String, Integer> phi(String param1String, Integer param1Integer,
					Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
				return DDDImpl.create(param1String, param1Integer, DDDImpl.create("H", Integer.valueOf(2),
						(DD<String, Integer>) phi(id(param1Map, param1Integer), new Object[0])));
			}

			protected DD<?, ?> phi1(Object... param1VarArgs) {
				return DDDImpl.create("T", Integer.valueOf(2));
			}

			public int computeHashCode() {
				return getClass().hashCode() * 7757;
			}

			public boolean isEqual(Object param1Object) {
				return (this == param1Object);
			}
		};
		
		Hom<String, Integer> composition = dDDHomImpl1.compose(dDDHomImpl2);
		
		DD<String, Integer>  object = composition.phi(dDD, new Object[0]);
		System.out.println(object);
		Assert.assertEquals(
				DDDImpl.create("b", Integer.valueOf(8), DDDImpl.create("H", Integer.valueOf(2), DDDImpl.create("H",
						Integer.valueOf(1),
						DDDImpl.create("H", Integer.valueOf(2), DDDImpl.create("T", Integer.valueOf(1),
								DDDImpl.create("H", Integer.valueOf(2), DDDImpl.create("T", Integer.valueOf(2)))))))),
				object);
	}

	@Test
	public void testRelocation() {
		System.out.println("****** Reloc ********");
		DDDRelocationHom<String, Integer> dDDRelocationHom = new DDDRelocationHom<String, Integer>("d");
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("d", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.append(DDDImpl.create("e", Integer.valueOf(3)));
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(5));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("b", Integer.valueOf(6)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("c", Integer.valueOf(7)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("d", Integer.valueOf(3)));
		dDD2 = (DD<String, Integer>) dDD2.append(DDDImpl.create("e", Integer.valueOf(3)));
		dDD1 = (DD<String, Integer>) dDD1.union(dDD2);
		System.out.println(dDD1);
		Object object = dDDRelocationHom.phi(dDD1);
		System.out.println("Reloc : " + object);
		DD<String, Integer> dDD3 = DDDImpl.create("d", Integer.valueOf(3));
		dDD3 = (DD<String, Integer>) dDD3.append(DDDImpl.create("a", Integer.valueOf(1)));
		dDD3 = (DD<String, Integer>) dDD3.append(DDDImpl.create("b", Integer.valueOf(2)));
		dDD3 = (DD<String, Integer>) dDD3.append(DDDImpl.create("c", Integer.valueOf(3)));
		dDD3 = (DD<String, Integer>) dDD3.append(DDDImpl.create("e", Integer.valueOf(3)));
		DD<String, Integer> dDD4 = DDDImpl.create("d", Integer.valueOf(3));
		dDD4 = (DD<String, Integer>) dDD4.append(DDDImpl.create("a", Integer.valueOf(5)));
		dDD4 = (DD<String, Integer>) dDD4.append(DDDImpl.create("b", Integer.valueOf(6)));
		dDD4 = (DD<String, Integer>) dDD4.append(DDDImpl.create("c", Integer.valueOf(7)));
		dDD4 = (DD<String, Integer>) dDD4.append(DDDImpl.create("e", Integer.valueOf(3)));
		dDD3 = (DD<String, Integer>) dDD3.union(dDD4);
		Assert.assertSame(object, dDD3);
	}
}

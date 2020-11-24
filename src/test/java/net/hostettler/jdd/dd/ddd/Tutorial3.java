package net.hostettler.jdd.dd.ddd;

import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ddd.DDDHomImpl;
import net.hostettler.jdd.dd.ddd.DDDImpl;

public class Tutorial3 {
	@Test
	public void testDDDHom() throws IOException {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1),
				DDDImpl.create("b", Integer.valueOf(2), DDDImpl.create("c", Integer.valueOf(3))));
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(1),
				DDDImpl.create("b", Integer.valueOf(42), DDDImpl.create("c", Integer.valueOf(3))));
		SetVarHom setVarHom = new SetVarHom("b", Integer.valueOf(42));
		System.out.println(setVarHom.phi(dDD1, new Object[0]));
		Assert.assertEquals(dDD2, setVarHom.phi(dDD1, new Object[0]));
	}

	private class SetVarHom extends DDDHomImpl<String, Integer> {
		private String mVariable;

		private Integer mValue;

		public SetVarHom(String param1String, Integer param1Integer) {
			this.mVariable = param1String;
			this.mValue = param1Integer;
		}

		protected DD<String, Integer> phi(String param1String, Integer param1Integer,
				Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
			return DDDImpl.create(param1String, this.mValue, (DD<String, Integer>) id(param1Map, param1Integer));
		}

		public boolean isLocallyInvariant(DD<String, Integer> param1DD) {
			return !((String) param1DD.getVariable()).equals(this.mVariable);
		}

		protected DD<String, Integer>phi1(Object... param1VarArgs) {
			return getAny();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 3643 + this.mVariable.hashCode() * 1667 + this.mValue.hashCode() * 1583;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof SetVarHom) {
				SetVarHom setVarHom = (SetVarHom) param1Object;
				bool = (this.mVariable.equals(setVarHom.mVariable) && this.mValue.equals(setVarHom.mValue)) ? true
						: false;
			}
			return bool;
		}
	}
}

package net.hostettler.jdd.dd.sdd;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.DDImpl;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.HomImpl;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.sdd.SDDHomImpl;
import net.hostettler.jdd.dd.sdd.SDDImpl;

public class TestCoverageTest {

	@Before
	public void setup() {
		DDImpl.resetCache();
		HomImpl.resetCache();
	}

	@Test
	public void testCreateCoverage() {
		DD<String, ValSet<Object>> recsType1009 = SDDImpl.create("CDE_FS_EBA_IFRS9",
				ObjSet.create("1030000", "1061000", "1071000", "1010100", "1011100", "1012100", "1013100", "1014100",
						"1015100", "1011200", "1012200", "1013200", "1014200", "1015200"),
				SDDImpl.create("RCA_FS_EBA_IFRS9",
						ObjSet.create(Double.valueOf(-1d), Double.valueOf(0.0d), Double.valueOf(1d),
								Double.valueOf(5000d), Double.valueOf(50000d), Double.valueOf(1000000d)),
						SDDImpl.create("TYP_CONSO_SCOPE", ObjSet.create("0", "1"))));
		
		System.out.println(recsType1009);
		System.out.println("States : " + recsType1009.getStates());

		Hom<String, ValSet<Object>> allocationRulesHom = 
				new Ifrs9Hom(s -> (s.equals("1030000")),
						v -> (!v.equals(0.0d)), s -> (s.equals("0") || s.equals("1")))
				.union(new Ifrs9Hom(s -> (s.equals("1061000")), v -> (!v.equals(0.0d)),
						s -> (s.equals("0") || s.equals("1")))
				.union(new Ifrs9Hom(s -> (s.equals("1071000")), v -> (!v.equals(0.0d)),
						s -> (s.equals("0") || s.equals("1"))))
				.union(new Ifrs9Hom(s -> (((String)s).startsWith("1072")), v -> (!v.equals(0.0d)),
						s -> (s.equals("0") || s.equals("1"))))				
				);

		DD<String, ValSet<Object>> actualRecsType1009 = allocationRulesHom.phi(recsType1009);
		System.out.println(actualRecsType1009);
		System.out.println("States : " + actualRecsType1009.getStates());
		
		DD<String, ValSet<Object>> missingTestData = recsType1009.difference(actualRecsType1009);

	}

	static class Ifrs9Hom extends SDDHomImpl<String, Object> {

		private Predicate<Object> cdeFilter;
		private Predicate<Object> rcaFilter;
		private Predicate<Object> typFilter;

		public Ifrs9Hom(Predicate<Object> cdeFilter, Predicate<Object> rcaFilter, Predicate<Object> typFilter) {
			this.cdeFilter = cdeFilter;
			this.rcaFilter = rcaFilter;
			this.typFilter = typFilter;
		}

		protected DD<String, ValSet<Object>> phi(String var, ValSet<Object> values,
				Map<ValSet<Object>, DD<String, ValSet<Object>>> alpha, Object... parameters) {

			ValSet<Object> newValues;
			switch (var) {
			case "CDE_FS_EBA_IFRS9":
				newValues = ObjSet.create(StreamSupport.stream(values.spliterator(), false)
						.filter(v -> cdeFilter.test(v)).collect(toUnmodifiableSet()));
				break;
			case "RCA_FS_EBA_IFRS9":
				newValues = ObjSet.create(StreamSupport.stream(values.spliterator(), false)
						.filter(v -> rcaFilter.test(v)).collect(toUnmodifiableSet()));
				break;
			case "TYP_CONSO_SCOPE":
				newValues = ObjSet.create(StreamSupport.stream(values.spliterator(), false)
						.filter(v -> typFilter.test(v)).collect(toUnmodifiableSet()));
				break;
			default:
				newValues = null;
				break;
			}

			return SDDImpl.create(var, newValues, phi(id(alpha, values)));
		}

		protected DD<String, ValSet<Object>> phi1(Object... param1VarArgs) {
			return this.getTrue();
		}

		@Override
		protected int computeHashCode() {
			final int prime = 31;
			int result = this.getClass().hashCode();
			result = prime * result + ((cdeFilter == null) ? 0 : cdeFilter.hashCode());
			result = prime * result + ((rcaFilter == null) ? 0 : rcaFilter.hashCode());
			result = prime * result + ((typFilter == null) ? 0 : typFilter.hashCode());
			return result;
		}

		@Override
		protected boolean isEqual(Object obj) {
			if (getClass() != obj.getClass())
				return false;
			Ifrs9Hom other = (Ifrs9Hom) obj;
			if (cdeFilter == null) {
				if (other.cdeFilter != null)
					return false;
			} else if (!cdeFilter.equals(other.cdeFilter))
				return false;
			if (rcaFilter == null) {
				if (other.rcaFilter != null)
					return false;
			} else if (!rcaFilter.equals(other.rcaFilter))
				return false;
			if (typFilter == null) {
				if (other.typFilter != null)
					return false;
			} else if (!typFilter.equals(other.typFilter))
				return false;
			return true;
		}
		
		
	}
}
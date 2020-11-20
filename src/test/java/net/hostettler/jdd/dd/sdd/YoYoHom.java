package net.hostettler.jdd.dd.sdd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.sdd.SDDHomImpl;
import net.hostettler.jdd.dd.sdd.SDDImpl;

public class YoYoHom<SDDTVar, SDDTVal, DDDTVar, DDDTVal> extends SDDHomImpl<SDDTVar, SDDTVal> {
	private Set<SDDTVar> mNode2Exec;
	private Hom<DDDTVar, DDDTVal> mHom;

	public YoYoHom(Set<SDDTVar> clusters, Hom<DDDTVar, DDDTVal> hom) {
		this.mNode2Exec = clusters;
		this.mHom = hom;
	}

	protected DD<SDDTVar, ValSet<SDDTVal>> phi(SDDTVar e, ValSet<SDDTVal> x, Map<ValSet<SDDTVal>, DD<SDDTVar, ValSet<SDDTVal>>> alpha,
			Object... parameters) {
		List<?> paramContainer = null;

		List<?> params = new ArrayList();
		if (parameters.length > 0) {

			paramContainer = (List) parameters[0];
		} else {
			paramContainer = new ArrayList();
		}

		DD<DDDTVar, DDDTVal> state = (DD) DDDImpl.DDD_FALSE;
		if (paramContainer.size() == 0) {
			state = this.mHom.phi( (DD) x, new Object[] { params });
		} else {
			for (Object o : paramContainer) {

				DD<DDDTVar, DDDTVal> d = this.mHom.phi( (DD) x, new Object[] { o, params });
				state = (DD<DDDTVar, DDDTVal>) state.union((DD) d);
			}
		}

		if (state == DDDImpl.DDD_FALSE) {
			return (DD) SDDImpl.SDD_FALSE;
		}

		if (params.size() > 0) {
			return SDDImpl.create(e, (ValSet) state,
					phi(id(alpha, x), new Object[] { params }));
		}

		return SDDImpl.create(e, (ValSet) state, (DD<SDDTVar, ValSet<SDDTVal>>) id(alpha, x));
	}

	public boolean isLocallyInvariant(DD<SDDTVar, ValSet<SDDTVal>> dd) {
		return (this.mNode2Exec.size() != 0 && !this.mNode2Exec.contains(dd.getVariable()));
	}

	protected DD<?, ?>  phi1(Object... parameters) {
		return SDDImpl.SDD_ANY;
	}

	protected boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof YoYoHom) {
			YoYoHom thatHom = (YoYoHom) that;
			eq = this.mNode2Exec.equals(thatHom.mNode2Exec);
			eq &= this.mHom.equals(thatHom.mHom);
		}

		return eq;
	}

	protected int computeHashCode() {
		return getClass().hashCode() * 977 ^ this.mNode2Exec.hashCode() * 347 ^ this.mHom.hashCode() * 2749;
	}

	public String toString() {
		return "yoyo(" + this.mNode2Exec + " ," + this.mHom + ")";
	}
}


package ch.unige.cui.smv.dd.util;

import ch.unige.cui.smv.dd.DD;
import java.util.HashSet;
import java.util.Set;

public class DDGraphGenerator {
	private Set<String> mAlreadyDone;

	public String outputDOTFormat(DD<Object, Object> sdd) {
		this.mAlreadyDone = new HashSet<String>();
		StringBuilder buffer = new StringBuilder();
		buffer.append(getPrefix());
		buffer.append(generate(sdd));
		buffer.append(getPostfix());
		return buffer.toString();
	}

	private String getPrefix() {
		return "digraph G { nodesep=0.2; rankdir=LR; node [shape=box,width=0.2,height=0.2];";
	}

	private String getPostfix() {
		return "}";
	}

	private String generate(DD<Object, Object> dd) {
		if (this.mAlreadyDone.contains(getNodeId(dd))) {
			return "";
		}
		this.mAlreadyDone.add(getNodeId(dd));

		String string = "";
		string = string + getNodeId(dd) + " [label=\"" + dd.getVariable() + "\"];\n";
		for (Object x : dd.getDomain()) {
			if (x instanceof DD) {
				string = string + getNodeId(dd, dd.getAlpha(x)) + "[shape=point label=\"\",width=.01,height=.01];\n";

				string = string + getNodeId(dd) + " ->" + getNodeId(dd, dd.getAlpha(x))
						+ "[arrowhead=none label=\"\",weight=8];\n";

				string = string + getNodeId(dd, dd.getAlpha(x)) + " ->" + getNodeId(dd.getAlpha(x))
						+ "[label=\"\",weight=8];\n";

				string = string + generate((DD) x) + "\n";
				string = string + getNodeId(dd, dd.getAlpha(x)) + " ->" + getNodeId(x)
						+ "[style=dashed, label=\"\",arrowhead = \"dot\",color=\"red\"];\n";
			} else {

				string = string + getNodeId(dd) + " ->" + getNodeId(dd.getAlpha(x)) + "[label=\"" + x.toString()
						+ "\", weight=8];\n";
			}

			string = string + generate(dd.getAlpha(x)) + "\n";
		}
		return string;
	}

	private String getNodeId(Object o) {
		return "n" + System.identityHashCode(o);
	}

	private String getNodeId(Object o1, Object o2) {
		return getNodeId(o1) + "_" + getNodeId(o2);
	}
}

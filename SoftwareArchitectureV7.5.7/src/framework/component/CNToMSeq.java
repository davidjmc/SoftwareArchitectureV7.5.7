package framework.component;

import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public abstract class CNToMSeq extends Component {
	private int NProvided, MRequired;

	public CNToMSeq(String name, int N, int M) {
		this.NProvided = N;
		this.MRequired = M;
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		for (int idx = 0; idx < NProvided; idx++)
			this.interfaces.add(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		for (int idx = 0; idx < MRequired; idx++)
			this.interfaces.add(new RequiredInterface(Utils.INTERFACE_TWO_WAY));
		this.semantics = new Semantics(
				"invP.e1->i_PosInvP->"+ // from srh
		"i_PreInvR1->invR.e2->i_PosInvR1->"+ // to marshaller
		"i_PreTerR->terR.e2->i_PosTerR1->"+ // from marshaller
		"i_PreInvR2->invR.e3->i_PosInvR2->"+ // to server
		"i_PreTerR->terR.e3->i_PosTerR2->"+ // from server 
		"i_PreInvR1->invR.e2->i_PosInvR1->"+ // to marshaller 
		"i_PreTerR->terR.e2->i_PosTerR1->"+ // from marshaller
		"i_PreTerP->terP.e1->i_PosTerP"); // to srh
	}
}
package framework.component;

import java.util.Arrays;

import framework.basic.Behaviour;
import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public abstract class C1To2Seq extends Component {
	
	public C1To2Seq(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.interfaces = Arrays.asList(
				new ProvidedInterface(Utils.INTERFACE_TWO_WAY),
				new RequiredInterface(Utils.INTERFACE_TWO_WAY),
				new RequiredInterface(Utils.INTERFACE_TWO_WAY));
		this.type = new Type(this);
		this.semantics = new Semantics(
				new Behaviour(new String(
						          "i_PreInvP  ; invP[t1] ; i_PosInvP ; "
								+ "i_PreInvR1 ; invR[t2] ; i_PosInvR ; i_PreTerR ; terR [t2] ; i_PosTerR1 ;"
								+ "i_PreInvR2 ; invR[t3] ; i_PosInvR ; i_PreTerR ; terR [t3] ; i_PosTerR2 ;"
								+ "i_PreTerP  ; terP[t1] ; i_PosTerP")));
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
	}
}
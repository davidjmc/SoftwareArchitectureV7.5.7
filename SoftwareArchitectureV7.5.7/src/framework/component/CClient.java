package framework.component;

import java.util.Arrays;

import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.RequiredInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public abstract class CClient extends Component {

	public CClient(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.interfaces = Arrays.asList(new RequiredInterface(Utils.INTERFACE_TWO_WAY));
		this.semantics = new Semantics("i_PreInvR -> invR.e1 -> i_PosInvR -> terR.e1 -> i_PosTerR [] i_PosTerR2");
	}
}
package framework.component;

import java.util.Arrays;

import framework.basic.Constraints;
import framework.basic.Identification;
import framework.basic.ProvidedInterface;
import framework.basic.RuntimeInfo;
import framework.basic.Semantics;
import framework.basic.Type;
import utils.Utils;

public abstract class CServer extends Component {

	public CServer() {
	}

	public CServer(String name) {
		this.id = new Identification(this.hashCode(), name);
		this.type = new Type(this);
		this.constraints = new Constraints();
		this.runtimeInfo = new RuntimeInfo();
		this.interfaces = Arrays.asList(new ProvidedInterface(Utils.INTERFACE_TWO_WAY));
		this.semantics = new Semantics("i_PreInvP -> invP.e1 -> i_PosInvP -> i_PreTerP -> terP.e1 -> i_PosTerP");
	}
}
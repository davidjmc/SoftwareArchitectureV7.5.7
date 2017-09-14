package middleware.distribution.clientproxy;

import java.io.Serializable;

import container.Broker;
import middleware.services.naming.AbsoluteObjectReference;

public class ClientProxy implements Serializable {
	private static final long serialVersionUID = 1L;
	private AbsoluteObjectReference aor;
	protected Broker broker;

	public ClientProxy() {
	}

	public AbsoluteObjectReference getAor() {
		return aor;
	}

	public void setAor(AbsoluteObjectReference aor) {
		this.aor = aor;
	}

	public Broker getBroker() {
		return broker;
	}

	public void setBroker(Broker broker) {
		this.broker = broker;
	}
}

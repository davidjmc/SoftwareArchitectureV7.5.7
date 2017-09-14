package adaptation;

import java.util.Iterator;

import container.ExecutionEnvironment;
import container.ExecutionUnit;
import framework.basic.Element;
import framework.configuration.StructureEdge;
import utils.MyError;
import utils.Utils;

public class Executor {
	private ExecutionEnvironment env;

	public Executor(ExecutionEnvironment e) {
		this.env = e;
	}

	public void executeFake(ExecutionEnvironment e) {

		if (e.getConf().hasElement("marshaller1")) {
			replaceComponent(env, "marshaller1", "marshaller2", "CMarshaller2");
		} else if (e.getConf().hasElement("marshaller2")) {
			replaceComponent(env, "marshaller2", "marshaller1", "CMarshaller1");
		}
	}

	public void replaceComponent(ExecutionEnvironment env, String oldElementName, String newElementName,
			String newElementType) {

		// -------- pause component
		env.getExecutionManager().pause(env,oldElementName);
				
		// -------- replace component in the configuration (Step 1)
		env.getConf().replaceElement(oldElementName, newElementName, newElementType);
		
		// -------- create internal queues
		env.getConf().configure(env);
			
		// -------- create external queues
		env.getExecutionManager().createQueues(env,2);
	
		// ------- update execution unit's elements
		Iterator<StructureEdge> tempIt = env.getConf().getStructure().edgeSet().iterator();
		boolean foundElement = false;
		Element newElementTemp = null;
		while (tempIt.hasNext() && !foundElement) {
			StructureEdge edgeTemp = tempIt.next();
			Element from = (Element) edgeTemp.getS();
			Element to = (Element) edgeTemp.getT();
			if (from.getIdentification().getName().contains(newElementName)) {
				newElementTemp = from;
				foundElement = true;
			} else if (to.getIdentification().getName().contains(newElementName)) {
				newElementTemp = to;
				foundElement = true;
			}
		}

		if (!foundElement){
			new MyError("Element " + newElementName + " not found in the structure's graph!",
					Utils.FATAL_ERROR).print();
		}
		
		foundElement = false;
		Iterator<ExecutionUnit> it = env.getExecutionManager().getexecutionUnits().iterator();
		while(!foundElement && it.hasNext()){
          ExecutionUnit unit = it.next();
			if (unit.getElement().getIdentification().getName().contains(oldElementName)) {
				int idx = env.getExecutionManager().getexecutionUnits().indexOf(unit);
				env.getExecutionManager().getexecutionUnits().get(idx).setElement(newElementTemp);
				foundElement = true;
			}
		}

		// resume execution units
		env.getExecutionManager().resume(env,newElementName);
	}
}

package adaptation;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import container.ExecutionEnvironment;

public class AdaptationManager {
	private static ExecutionEnvironment env;
	private static ArrayList<String> args;

	public AdaptationManager(ExecutionEnvironment e) {
		AdaptationManager.env = e;
		AdaptationManager.args = new ArrayList<String>();
	}

	public void execute() {
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);

		// configure parameters of adapter
		configureParameters();
		
		ThreadMAPEK threadMAPEK = new ThreadMAPEK(env);

		ScheduledFuture<?> scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(threadMAPEK,
				(int) AdaptationManager.env.getParameters().get("prom-delay-first-checking"),
				(int) AdaptationManager.env.getParameters().get("prom-delay-between-checking"), TimeUnit.MILLISECONDS);

		if (scheduledFuture.isDone())
			scheduledExecutor.shutdown();
	}

	public ExecutionEnvironment getEnv() {
		return AdaptationManager.env;
	}

	public void configureParameters() {
		args.add(env.getParameters().get("prom-property").toString());
		args.add(env.getParameters().get("prom-property-parameter-a").toString());
		args.add(env.getParameters().get("prom-property-parameter-b").toString());
		args.add(env.getParameters().get("prom-property-parameter-c").toString());
		args.add(env.getParameters().get("prom-begin-element").toString());
		args.add(env.getParameters().get("prom-end-element").toString());
		args.add(env.getParameters().get("prom-begin-action").toString());
		args.add(env.getParameters().get("prom-end-action").toString());
	}

	public void setEnv(ExecutionEnvironment env) {
		AdaptationManager.env = env;
	}

	private static class ThreadMAPEK implements Runnable {
		private ExecutionEnvironment env;
		public ThreadMAPEK(ExecutionEnvironment e) {
			this.env = e;
		}
		
		@Override
		public void run() {
			//MonitorLog monitorLog = new MonitorLog();
			//MonitorTBD monitorTBD = new MonitorTBD();
			//Analyser analyser = new Analyser(this.env);
			//Planner planner = new Planner(this.env);
			Executor executor = new Executor(this.env);

			// control loop
			//String dataFileName = monitorLog.monitor();
			//AnalysisStatus analysisStatus = analyser.analyse(dataFileName, args);
			// AdaptationPlan adaptationPlan =
			// planner.selectPlan(analysisStatus);
			//executor.execute(adaptationPlan);
			//System.out.println(this.getClass()+" **************** CHANGE **************"+System.nanoTime());
			executor.executeFake(this.env);
			//executeFake();
		}
	}
}


package put.two.to.contest.client;

import java.util.ArrayList;

import put.two.to.contest.model.CarriersCollection;
import put.two.to.contest.model.Package;
import put.two.to.contest.model.Result;
import put.two.to.contest.model.Warehouse;

public class ConsoleClient {
	public static boolean process(String input, String output, final int time, final int iterations, final boolean messing, boolean print) {
		long start = System.currentTimeMillis();

		final Warehouse warehouse = new Warehouse();
		try {
			warehouse.readData(input);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}

		final Processing processing = new Processing(warehouse);

		for (int th = 0; th < processing.N_THREADS; th++) {
			final float mul = 1 - (float) th / processing.N_THREADS;
			new Thread(new Runnable() {

				@Override
				public void run() {
					CarriersCollection collection = new CarriersCollection(
							warehouse.getPallets(), warehouse.getPackages());

					int iterationsCount = 0;
					do {
						ArrayList<Package> list = new ArrayList<>(processing.currentOrder);
						if (messing && ((iterationsCount + 3) % 6 == 0)){
							list = collection.random(list, 1);
						}
						Result result = collection.process(list,
								processing.currentTemp, mul, time);

						processing.setResult(result);

						processing.waitThread();
					} while (!processing.end);
				}
			}).start();
		}

		float N_ITER = iterations;
		for (int i = 1; i <= N_ITER; i++) {
			processing.sync(output, 1 - (float) i / N_ITER);
		}

		long end = System.currentTimeMillis();

		if (print) {
			System.out.print(processing.bestResult.getArea() + "\t"
					+ processing.bestResult.getVolume() + "\t");
			System.out.println("Took " + (end - start) + " ms. "
					+ processing.bestResult.count);
		}

		return true;
	}

	public static void main(String[] args) {
		for (int i = 1; i <= 10; i++) {
			String input;
			String output;

			if (i < 10) {
				input = "data/instances-pp1/pp10" + i + ".in";
				output = "data/instances-pp1/output10" + i + ".txt";
			} else {
				input = "data/instances-pp1/pp1" + i + ".in";
				output = "data/instances-pp1/output1" + i + ".txt";
			}

			final int N = 11;
			for (int it = 0; it < N; it++)
				process(input, output, 49, 20, true, true);
		}
	}
}

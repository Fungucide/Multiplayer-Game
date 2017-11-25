package GUI;

public class RenderUpdate implements Runnable {
	private final Render r;
	private final int FPS;
	private final int DELAY;

	public RenderUpdate(Render r, int fps) {
		this.r = r;
		this.FPS = fps;
		DELAY = 1000 / FPS;
	}

	public void update() throws InterruptedException {
		while (true) {
			r.repaint();
			Thread.sleep(DELAY);
		}
	}

	@Override
	public void run() {
		try {
			update();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import javax.swing.JFrame;

public class Main {
	
	private Canvas canvas;
	private BufferStrategy bs;
	private Graphics g;
	private int width = 640;
	private int height = 480;
	
	private Executor executor = Executors.newSingleThreadExecutor();
	
	public Main() {
		EventQueue.invokeLater(() -> {canvas = new Canvas();
			canvas.setPreferredSize(new Dimension(width, height));
			
			JFrame frame = new JFrame("Fractal");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.add(canvas);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
			canvas.createBufferStrategy(1);
			bs = canvas.getBufferStrategy();
			g = bs.getDrawGraphics();
			
			calculate();
		});
	}
	
	public void calculate() {
		executor.execute(() -> {
			for(int py = 0; py < canvas.getHeight(); py++) {
				for(int px = 0; px < canvas.getWidth(); px++) {
					float x0 = (((px - 0f) / (width - 0f)) * (0.47f - -2f)) + -2f;
					float y0 = (((py - 0f) / (height - 0f)) * (1.12f - -1.12f)) + -1.12f;
					float x = 0f;
					float y = 0f;
					int iteration = 0;
					int maxIteration = 5000;
					
					while((x * x + y * y) <= 4f && iteration < maxIteration) {
						float xtemp = (x * x - y * y) + x0;
						y = (2f * x * y) + y0;
						x = xtemp;
						iteration++;
					}
					
					int red = 255 - (iteration % 16) * 16;
					int green = (16 - iteration % 16) * 16;
					int blue = (iteration % 16) * 16;
					int color = (red << 16) | (green << 8) | blue;
					Color c = new Color(color);
					g.setColor(c);
					g.fillRect(px, py, 1, 1);
				}
			}
		});
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}
}

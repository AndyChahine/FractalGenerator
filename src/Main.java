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
		/* encapuslates all of this inside a runnable object whos run method is then called by the event dispatch thread
		 * so it plays nicely with swing
		 * lambda expression equivalent to
		 * EventQueue.invokeLater(new Runnable(){
		 * 		@Override
		 * 		public void run(){
		 * 			// creation code inside invokeLater() from down below
		 * 		}
		 * });
		*/ 
		EventQueue.invokeLater(() -> {
			// create canvas to have access to graphics object
			canvas = new Canvas();
			canvas.setPreferredSize(new Dimension(width, height));
			
			// create jframe to display a window and hold canvas
			JFrame frame = new JFrame("Fractal");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.add(canvas);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
			// create 1 back buffer for canvas to draw to
			canvas.createBufferStrategy(1);
			// get the back buffer from canvas
			bs = canvas.getBufferStrategy();
			// finally get graphics object from back buffer
			g = bs.getDrawGraphics();
			
			// calculate and render mandelbrot set
			calculate();
		});
	}
	
	public void calculate() {
		// using java's executor thread to run calculations separately
		executor.execute(() -> {
			
			// nested for loop to iterate over each pixel (px, py) in the range [0, width] and [0, height]
			for(int py = 0; py < canvas.getHeight(); py++) {
				for(int px = 0; px < canvas.getWidth(); px++) {
					// using inefficient "escape time algorithm" to calculate if a pixel lies close to the mandelbrot set based on a number of maximum iterations
					// x coordinate of pixel is scaled to lie in the mandelbrot x range [-2, 1]
					float x0 = (((px - 0f) / (width - 0f)) * (1f - -2f)) + -2f;
					// y coordinate of pixel is scaled to lie in the mandelbrot y range [-1, 1]
					float y0 = (((py - 0f) / (height - 0f)) * (1f - -1f)) + -1f;
					float x = 0f;
					float y = 0f;
					float iteration = 0f;
					float maxIterations = 1000f;
					
					// the iterative function Zₙ₊₁ = Zₙ² + C starting with Z₀ = 0 and a Complex number C
					// the value C is an element of the mandelbrot set if its value is unbounded based on an arbitrary threshold, in this case, maxIterations
					// z = x + 𝓲y
					// z² = x² + 𝓲2xy - y²
					// c = x₀ + 𝓲y₀
					// the while loop checks to see if the abs(z) < 2 and the number of iterations is less than our threshold, maxIterations to see if
					// it goes off to infinity or is bounded
					// the operations done here are done without using a complex-data-type
					while(((x * x) + (y * y)) <= 4 && iteration < maxIterations) {
						// first taking the real part of the function and assigning it to x
						// x = Re(z² + c) = Re((x² + 𝓲2xy - y²) + (x₀ + 𝓲y₀)) = x² - y² + x₀
						float xtemp = ((x * x) - (y * y)) + x0;
						// then taking the imaginary part of the function and assigning it to y
						// y = Im(z² + c) = Im((x² + 𝓲2xy - y²) + (x₀ + 𝓲y₀)) = 2xy - y₀
						y = (2f * x * y) + y0;
						x = xtemp;
						iteration++;
					}
					
					// calculate the HSV color based on the number of iterations
					float hue = (float) (Math.pow(((iteration / maxIterations) * 20f), 1.5f) % 360f);
					float saturation = 1f;
					float value = 1f;
					
					// if iterations are greater than or equal to our threshold then our complex number C is a part of the mandelbrot set and we set its color to black
					if(iteration >= maxIterations) {
						value = 0f;
					}
					
					// convert HSV color to RGB
					int rgb = Color.HSBtoRGB(hue, saturation, value);
					Color color = new Color(rgb);
					
					// draw pixel at our coordinate (px, py) with our calculated color
					g.setColor(color);
					g.fillRect(px, py, 1, 1);
				}
			}
		});
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}
}

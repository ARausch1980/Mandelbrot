import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mandelbrot extends JFrame {
	
	public List<double[]> points = new ArrayList<double[]>();
	
	int iter = 300;
	double xMin = -1.5;
	double xMax = 0.5;
	
	double yMin = -1;
	double yMax = 1;
	
	boolean COLOR = false;
	
	double tempX;
	double tempY;
	
	JPanel panel;
	
	public static void main(String[] args) {
		
		Mandelbrot mandelbrot = new Mandelbrot();
	}
	
	public Mandelbrot() {
			panel = new JPanel() {
			
			public void paintComponent(Graphics g1) {
				double scale = Math.min(2000 / (xMax - xMin), 1000 / (yMax - yMin));
				final List<double[]> pointList = new ArrayList<double[]>(points);
				super.paintComponent(g1);

				Graphics2D g2d = (Graphics2D) g1;

				for (double[] p : pointList) {
					float r = 0;
					float g = 0;
					float b = 0;
					double val = p[2];
					
					if (COLOR) {
						if (val == (double) (iter - 1)) {
							r = 1;
							g = 1;
							b = 1;
						} else if (val > 1 && val <= (iter / 6) * 1) {
							r = 0;
							g = (float) 100 / 255;
							b = (float) 149 / 255;
						} else if (val > (iter / 6) * 1 && val <= (iter / 6) * 2) {
							r = 0;
							g = (float) 76 / 255;
							b = (float) 112 / 255;
						} else if (val > (iter / 6) * 2 && val <= (iter / 6) * 3) {
							r = 0;
							g = (float) 147 / 255;
							b = (float) 209 / 255;
						} else if (val > (iter / 6) * 3 && val <= (iter / 6) * 4) {
							r = (float) 242 / 255;
							g = (float) 99 / 255;
							b = (float) 95 / 255;
						} else if (val > (iter / 6) * 4 && val <= (iter / 6) * 5) {
							r = (float) 244 / 255;
							g = (float) 208 / 255;
							b = (float) 12 / 255;
						} else if (val > (iter / 6) * 5 && val <= (iter / 6) * 6) {
							r = (float) 224 / 255;
							g = (float) 160 / 255;
							b = (float) 37 / 255;
						} else {
							r = 0;
							g = 0;
							b = 0;
						}
						g2d.setColor(new Color(r, g, b));
					} else {
						float v = (float) ((val - 1) / (iter - 1));
						g2d.setColor(new Color(v, v, v));
					}
					double x = (p[0] * scale) + (-1 * xMin * scale); //200, 200, 250
					double y = (p[1] * scale) + (-1 * yMin * scale);
					g2d.draw(new Line2D.Double(x, y, x, y));
				}
			}
		};
		
		panel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				tempX = e.getX();
				tempY = e.getY();
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				double scale = Math.min(2000 / (xMax - xMin), 1000 / (yMax - yMin));
				double x2 = e.getX();
				double y2 = e.getY();
				
				if (Math.abs(x2 - tempX) < 10 || Math.abs(y2 - tempY) < 10) {
					return;
				}
				
				xMin = (Math.min(tempX, x2) - (-1 * xMin * scale)) / scale;
				xMax = (Math.max(tempX, x2) - (-1 * xMin * scale)) / scale;
				
				yMin = (Math.min(tempY, y2) - (-1 * yMin * scale)) / scale;
				yMax = (Math.max(tempY, y2) - (-1 * yMin * scale)) / scale;
				
				scale = Math.min(2000 / (xMax - xMin), 1000 / (yMax - yMin));
				
				System.out.println("xMin/Max: " + xMin + ", " + xMax);
				System.out.println("yMin/Max: " + yMin + ", " + yMax);
				
				calcPoints();
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
		});
		
		panel.setPreferredSize(new Dimension(2000, 1000));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(panel);
		this.pack();
		this.setVisible(true);
		
		calcPoints();
	}
	
	public void calcPoints() {
		points.clear();
		double scale = Math.min(2000 / (xMax - xMin), 1000 / (yMax - yMin));
		double delta = 1 / scale;
		iter = (int) (4798 + (250 - 4798) / (1 + Math.pow(scale / 100000 / 2210915, 0.218)));
		System.out.println("iter: " + iter);
		System.out.println("scale: " + scale);
		for (double a = xMin ; a <= xMax ; a+= delta) {
			for (double b = yMin ; b <= yMax ; b+= delta) {
				if (isInCircle(a, b, 2)) {
					double[] c = {a, b, 0};
					isInSet(iter, c, c);
					points.add(new double[] {c[0], c[1], c[2]});
					panel.repaint();
				}
			}
		}
	}
	
	public boolean isInSet(int iter, double[] c, double[] z) {
		if (iter == 1 || magnitude(z) > 2) {
			c[2] = iter;
			return (magnitude(z) <= 2);
		}
		double[] newZ = sumComplexNumbers(getComplexSquare(z), c);
		iter--;
		return isInSet(iter, c, newZ);
	}
	
	public double magnitude(double[] c) {
		return Math.sqrt((Math.pow(c[0], 2)) + (Math.pow(c[1], 2)));
	}
	
	public double[] getComplexSquare(double[] c) {
		double[] result = {0, 0};
		
		result[0] += Math.pow(c[0], 2);
		result[0] += -1 * Math.pow(c[1], 2);
		result[1] += 2 * (c[0] * c[1]);
		
		return result;
	}
	
	public double[] sumComplexNumbers(double[] a, double[] b) {
		return new double[] {a[0] + b[0], a[1] + b[1]};
	}
	
	public boolean isInCircle(double a, double b, double rad) {
		return ((Math.pow(a, 2) + Math.pow(b, 2)) <= Math.pow(rad, 2));
	}
	
}

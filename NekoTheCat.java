//---------------------------------------------------------------------------------------------------
//
//	ECE 492 Java Laba 7: Neko the Cat
//	Joseph Homenick
//	March 31 2023
//	
//	Description: This program implements a simple game called Neko the Cat in which a cat (called Neko, famous in Japan)
//	tries to get its red ball. The player moves the cat's ball around by clicking the mouse cursor on the screen.
//	The cat adjusts its pursuit to try to get to the ball's location. When Neko the Cat gets its ball, the game is over.
//
//---------------------------------------------------------------------------------------------------
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class NekoTheCat implements MouseListener, Runnable
{
	//Images
	Image catRight1 = new ImageIcon(getClass().getResource("neko1.gif")).getImage();
	Image catRight2 = new ImageIcon(getClass().getResource("neko2.gif")).getImage();
	Image catLeft1  = new ImageIcon(getClass().getResource("neko3.gif")).getImage();
	Image catLeft2  = new ImageIcon(getClass().getResource("neko4.gif")).getImage();
	Image redBall   = new ImageIcon(getClass().getResource("red-ball.gif")).getImage();
	Image cat1 = catRight1;
	Image cat2 = catRight2;
	
	//Graphic Objects
	JFrame gameWindow = new JFrame("Neko The Cat!");
	JPanel gamePanel  = new JPanel();
	
	//Instance Variables
	int     catxPosition  = 1;    
	int     catyPosition  = 50;
	int     catWidth      = catRight1.getWidth(gamePanel);
	int     catHeight     = catRight1.getHeight(gamePanel);
	int     ballxPosition = 0;
	int     ballyPosition = 0;
	int     ballSize      = redBall.getWidth(gamePanel); 
	int     sleepTime     = 100; 												//pause time between image repaints (in ms)
	int     xBump         = 10;  												//amount (in pixels) cat image is moved each repaint.
	boolean catIsRunningToTheRight = true; 										//initially
	boolean catIsRunningToTheLeft  = false;										//initially
	boolean ballHasBeenPlaced      = false;										//initially
	Graphics g;
	AudioClip soundFile = Applet.newAudioClip(getClass().getResource("spacemusic.au"));

	public NekoTheCat()
	{
		
		gameWindow.getContentPane().add(gamePanel, "Center");
		gamePanel.setBackground(Color.white);
		gameWindow.setSize(500,400);   											//width,height
		gameWindow.setLocation(500,0);
		gameWindow.setVisible(true);   											//show the window!
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 				//terminate the program when the user
		g = gamePanel.getGraphics();
		//show game instructions on the screen
		g.setFont(new Font("Arial", Font.BOLD, 14)); 						//Font name, style, size
		g.drawString("Neko the cat is looking for its red ball!"         ,50,100); // String, x, y
		g.drawString("Click the mouse to place Neko's ball."              ,50,120);
		g.drawString("Can you move the ball to keep Neko from getting it?",50,140);
		g.drawString("Pull window larger to make the game easier."       ,50,160);
		g.drawString("Set window size to start playing!"       ,50,180);
		gamePanel.addMouseListener(this); 										//call me when a mouse action/event happens!
		soundFile.loop(); 
		new Thread(this).start();												//Create and start new thread	
	}

	public static void main(String[] args)
	{
		new NekoTheCat();
	}

	
	public void run()
	{
		Image currentImage = cat1;
		
		while (true)
		{
			while ((catxPosition > 0) &&  (catxPosition < gamePanel.getSize().width)) 
		    {
		    	g = gamePanel.getGraphics(); // get g again in case user has resized the window! 
		    	// move Neko again in the current direction
		    	
		    	// 1. Blank out the last image
				g.setColor(Color.white); 
			    g.fillRect(catxPosition, catyPosition, catWidth, catHeight); //x of upper-left-corner, y of upper-left-corner, width, height
		        // 2. Bump the location for the new image
			    catxPosition += xBump;
		        // 3. Select the next image.
				if (currentImage == cat1) 
				{
					currentImage = cat2;
				}
			    else
			    {	
			    	currentImage = cat1;
			    }
		        // 4. Draw the next cat image
			    g.drawImage(currentImage,  catxPosition, catyPosition, gamePanel);
		        // 5. Pause briefly to let human eye see the new image!
			    try {Thread.sleep(sleepTime);}
			    catch(InterruptedException ie){}
			    // 6. If necessary, redirect cat toward the ball
		    	if (ballHasBeenPlaced)
		    	{
		    		//If cat is below the ball, then move cat up 1 line
		    		if (catyPosition < ballyPosition) //larger y values = closer to bottom of panel
		    		{
		    			catyPosition += 5;
		    		}
		    		
		    		//If cat is above the ball, then move cat down 1 line
		    		if (catyPosition > ballyPosition) //larger y values = closer to bottom of panel
		    		{
		    			catyPosition -= 5;
		    		}
		    		
		    		//If the cat is running to the left and the ball is to the right of the cat, reverse cat's direction
		    		if (catIsRunningToTheLeft && (ballxPosition > catxPosition))
		    		{
		    			reverseDirectionFromLeftToRight();
		    		}
		    		
		    		//If the cat is running to the right and the ball is to the left of the cat, reverse cat's direction
		    		if (catIsRunningToTheRight &&(ballxPosition < catxPosition))
		    		{
		    			reverseDirectionFromRightToLeft();
		    		}
		    	}
		    	// 7. Proximity test to see if Neko is at the ball
		    	if ( (Math.abs(catyPosition - ballyPosition) < 10) && (Math.abs(catxPosition - ballxPosition) < 10) )
		    	{
		    		//Neko got the ball! End game!
		    		gamePanel.removeMouseListener(this);
		    		g.setColor(Color.red);
		    		g.setFont(new Font("Arial", Font.BOLD, 20));
		    		g.drawString("At last, I have my ball!",50,100); // String, x, y
		    		g.drawString("GAME OVER!",50,140);
		    		soundFile.stop();
		    		return;
		    	}
		    }
		    // Reached edge of window! - turn Neko around.
		    if (catxPosition > gamePanel.getSize().width)
		    {
		       reverseDirectionFromRightToLeft();
		       catxPosition = gamePanel.getSize().width -1;
		    }
		    
		    if (catxPosition < 0)
		    {
		       reverseDirectionFromLeftToRight();
		       catxPosition = 1;
		    }    
		   
		}
	}

	
	public void mouseClicked(MouseEvent me)
	{
		ballHasBeenPlaced = true;
		//1. Erase the old ball image at the previous x,y location
		g.setColor(Color.white); 
	    g.fillRect(ballxPosition, ballyPosition, ballSize, ballSize);
	    //2. Update x and y coordinates to where the mouse was just clicked
		ballxPosition = me.getX();
	    ballyPosition = me.getY();
	    //3. Draw the red ball at the new location
	    g.drawImage(redBall,  ballxPosition,ballyPosition,gamePanel);
	}
	
	private void reverseDirectionFromRightToLeft()
    {
		xBump = -xBump; // reverse increment
		cat1 = catLeft1;
		cat2 = catLeft2;
		catIsRunningToTheLeft  = true;
		catIsRunningToTheRight = false;
		return;
    }

	private void reverseDirectionFromLeftToRight()
    {
		xBump = -xBump;	
		cat1 = catRight1;
		cat2 = catRight2;
		catIsRunningToTheRight = true;
		catIsRunningToTheLeft  = false;
		return;
    }

	
	public void mousePressed(MouseEvent e)
	{
		
		
	}

	
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

}

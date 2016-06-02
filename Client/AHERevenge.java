/*
All Hallow's Eve - Revenge

Instructions:
Move mouse to aim
Press left click to shoot
FOR SOLO: save up money and upgrade to take down some zombie madness
FOR MULTI: team up with a friend over LAN to shoot down some zombies

Objective:
Survive all waves to save your soul

Features:
-Multiplayer - networked - waves are continuous with a defense buff every 4 waves (wave 4 and 8)
-Singleplayer - has upgrade features

 */ 

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import sun.audio.*;

public class AHERevenge extends JFrame implements ActionListener,KeyListener,MouseListener, MouseMotionListener{
	
	//JComponents
	public JDBPanel gpanel;
	public Timer ATimer; 
	public JButton solo;
	public JButton team;
	public JButton instructions;
	public JButton connect;
	public JButton main;
	public JButton nextwave;
	public JButton Repair;
	public JButton Upgrade;
	public JButton Buy1;
	public JButton Buy2;
	public JButton Equip[] = new JButton[3]; //For equipping of the 3 weps available
	public JLabel BG;
	public JOptionPane opane1;
	
	public double dblTimer;
	//BOOLEANS throughout the game
	public boolean blnStart = false;
	public boolean blnSolo = false;
	public boolean blnSpawned = false;
	public boolean blnfileRead = false;
	public boolean blnWaveDead = false;
	public boolean blnZombieDead[] = new boolean[10];
	public boolean blnServerMode = false;
	public boolean blnClientMode = false;
	public boolean blnZombieStart = false;
	public boolean blnShot = false;
	public boolean blnAlive = false;
	public boolean blnOtherPShot = false;
	public boolean blnDelaying = false;
	public boolean blnUpgrading = false;
	public boolean blnLose = false;
	public boolean blnBought1 = false;
	public boolean blnBought2 = false;
	public int intWaveWins = 0;
	public int intMouseX1;
	public int intMouseY1;
	public int intMouseX2;
	public int intMouseY2;
	public int intGunDmg = 10;
	public String strGunType = "magnum";
	public int intDelay = 0;
	public String strWaves[] = new String[9];
	
	//Network Related JComponents and important variables
	public JButton Server;
	public JButton Client;
	public JTextField IP;
	public JTextField port;
	public JTextArea connections;
	public JTextField textin;
	public JTextField chat;
	public JScrollPane spane;
	public SuperSocketMaster netcon;
	public String strIncoming;
	public int intMouseP1[] = new int[2];
	public int intMouseP2[] = new int[2];
	public int intPort;
	
	//Constructor
	public AHERevenge(){
		super();
		this.setTitle("All Hallow's Eve - Revenge");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100,100,500,630);
		this.setResizable(false); //Sets is so that user cannot maximize the game
		ATimer = new Timer(33,this); 
		//33ms = approx 30 FPS
		//this will activate the keyListener on the entire frame (program)
		gpanel = new JDBPanel(); //Creates the double buffered panel
		Container frame = this.getContentPane();
	  	frame.add(gpanel);
		this.addKeyListener(this);	
		gpanel.addMouseListener(this);
		gpanel.addMouseMotionListener(this);
		gpanel.setLayout(null); //creates the layout of gpanel
		
		//Buttons
		solo = new JButton("Singleplayer");
		solo.setBounds(100,520,110,30);
		solo.addActionListener(this);
		gpanel.add(solo);
		
		team = new JButton("Multiplayer");
		team.setBounds(250,520,110,30);
		team.addActionListener(this);
		gpanel.add(team);
		
		instructions = new JButton("Instructions");
		instructions.setBounds(358,5,110,20);
		instructions.addActionListener(this);
		gpanel.add(instructions);
		
		main = new JButton("Main Menu >>");
		main.setBounds(320,550,130,30);
		main.addActionListener(this);
		gpanel.add(main);
		main.setVisible(false);
		
		nextwave = new JButton("Next Wave >>");
		nextwave.setBounds(320,550,130,30);
		nextwave.addActionListener(this);
		gpanel.add(nextwave);
		nextwave.setVisible(false);
	
		Repair = new JButton("Repair");
		Repair.setBounds(320,175,100,20);
		Repair.addActionListener(this);
		gpanel.add(Repair);
		Repair.setVisible(false);
		
		Upgrade = new JButton("Upgrade");
		Upgrade.setBounds(320,210,100,20);
		Upgrade.addActionListener(this);
		gpanel.add(Upgrade);
		Upgrade.setVisible(false);
		
		Buy1 = new JButton("Buy"); //BUYING SHOTGUN
		Buy1.setBounds(340,290,70,20);
		Buy1.addActionListener(this);
		gpanel.add(Buy1);
		Buy1.setVisible(false);
		
		Buy2 = new JButton("Buy"); //BUYING SNIPER
		Buy2.setBounds(390,340,70,20);
		Buy2.addActionListener(this);
		gpanel.add(Buy2);
		Buy2.setVisible(false);
		
		for(int intCounter = 0;intCounter < 3;intCounter++){
			Equip[intCounter] = new JButton("Equip");
			Equip[intCounter].addActionListener(this);
			gpanel.add(Equip[intCounter]);
			Equip[intCounter].setVisible(false);	
		}
		Equip[0].setBounds(320,380,70,20); //magnum(default)
		Equip[0].setEnabled(false); //equipped by default
		Equip[1].setBounds(340,290,70,20); //shotgun
		Equip[2].setBounds(390,340,70,20); //sniper
		
		//NETWORK RELATED		
		Server = new JButton();
		Server.setBounds(130,95,20,20);
		Server.addActionListener(this);
		gpanel.add(Server);
		Server.setVisible(false);
		
		Client = new JButton();
		Client.setBounds(130,133,20,20);
		Client.addActionListener(this);
		gpanel.add(Client);
		Client.setVisible(false);
			
		connect = new JButton("Connect");
		connect.setBounds(20,333,100,30);
		connect.addActionListener(this);
		gpanel.add(connect);
		connect.setVisible(false);
		
		//TEXT FIELDS - Network related
		IP = new JTextField("");
		IP.setBounds(20,210,100,25);
		gpanel.add(IP);
		IP.setVisible(false);
		
		port = new JTextField("6112");
		port.setBounds(20,280,100,25);
		gpanel.add(port);
		port.setVisible(false);
		
		textin = new JTextField("");
		textin.setBounds(10,70,200,30);
		gpanel.add(textin);
		textin.addActionListener(this);
		textin.setVisible(false);
		
		chat = new JTextField("");
		chat.setBounds(20,540,200,25);
		gpanel.add(chat);
		chat.addActionListener(this);
		chat.setVisible(false);
		
		//Text Area - Used for chatting and multiplayer wait screen
		connections = new JTextArea("Waiting For Connections");
		spane = new JScrollPane();
		spane.setBounds(20,380,200,150);
		gpanel.add(spane);
		spane.getViewport().add(connections);
		connections.setEditable(false); //Player cannot type in this box
		spane.setVisible(false);
		
		//Image Icons
		ImageIcon icon = new ImageIcon (this.getClass().getResource("Game/mainmenu.jpg")); //creates an imageicon to put into the JLabel BG
		BG = new JLabel(icon);
		BG.setBounds(0,0,500,630);
		gpanel.add(BG);//Background is added to a label
		
		//Cursor = Sets the cursor to a different image 
		Toolkit toolkit = Toolkit.getDefaultToolkit(); //Creates a new toolkit to import an image into
		Image images = toolkit.getImage(this.getClass().getResource("Players/customcursor.png")); //Imports the image
		Point hotSpot = new Point(0,0);  //Sets the reference point for the pointer on cursor
		Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Custom Cursor"); //Creates the new cursor
		setCursor(cursor); //Finally sets the cursor to replace current mouse cursor
	}
	
	public void actionPerformed(ActionEvent theEvent){
		if(theEvent.getSource() == ATimer){	
			if(blnAlive){ //As long as the player is alive
				if(blnfileRead == false){
					fileread("wave.txt"); // Reads from a file called wave txt which is tells the program how strong the waves are
					blnfileRead = true; //Makes sure that the file is only read once in the whole game
				}
				 if(blnSpawned == false){ 
					 //Spawns the zombies according to text file read
					if(strWaves[gpanel.intWave].equalsIgnoreCase("weak")){ //Checks the array for strength of the wave
						for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){ //all weak zombies
							gpanel.zombies[intCounter2] = new zombie();//Creates the new zombie
							gpanel.zombies[intCounter2].drawme();  //Method for spawning the zombies (see below for details)  
						} 
						blnSpawned = true;
					}else if(strWaves[gpanel.intWave].equalsIgnoreCase("weak-medium")){
						for(int intCounter2 = 0;intCounter2 <= 8;intCounter2 = intCounter2 + 2){ //5 weaks for zombies
							gpanel.zombies[intCounter2] = new zombie();
							gpanel.zombies[intCounter2].drawme();    
						} 
						for(int intCounter3 = 1;intCounter3 <= 9;intCounter3 = intCounter3 + 2){ // 5 mediums
							gpanel.zombies[intCounter3] = new zombie("medium",100,2);
							gpanel.zombies[intCounter3].drawme();    
						} 
						blnSpawned = true;
					}else if(strWaves[gpanel.intWave].equalsIgnoreCase("medium")){
						for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){ // Full wave of medium
							gpanel.zombies[intCounter2] = new zombie("medium",100,2);
							gpanel.zombies[intCounter2].drawme();    
						} 
						blnSpawned = true;
					}else if(strWaves[gpanel.intWave].equalsIgnoreCase("weak-hard")){	
						for(int intCounter2 = 0;intCounter2 <= 8;intCounter2 = intCounter2 + 2){ //5 weaks for zombies
							gpanel.zombies[intCounter2] = new zombie();
							gpanel.zombies[intCounter2].drawme();    
						} 
						for(int intCounter3 = 1;intCounter3 <= 9;intCounter3 = intCounter3 + 2){ // 5 hard
							gpanel.zombies[intCounter3] = new zombie("strong",150,4);
							gpanel.zombies[intCounter3].drawme();    
						} 
						blnSpawned = true;
					}else if(strWaves[gpanel.intWave].equalsIgnoreCase("medium-hard")){
						for(int intCounter2 = 0;intCounter2 <= 8;intCounter2 = intCounter2 + 2){ //5 medium
							gpanel.zombies[intCounter2] = new zombie("medium",100,2);
							gpanel.zombies[intCounter2].drawme();    
						} 
						for(int intCounter3 = 1;intCounter3 <= 9;intCounter3 = intCounter3 + 2){ // 5 hard
							gpanel.zombies[intCounter3] = new zombie("strong",150,4);
							gpanel.zombies[intCounter3].drawme();    
						} 
						blnSpawned = true;
						}else if(strWaves[gpanel.intWave].equalsIgnoreCase("hard")){
						for(int intCounter2 = 0;intCounter2 <= 8;intCounter2 = intCounter2 + 2){ //5 hard
							gpanel.zombies[intCounter2] = new zombie("strong",150,4);
							gpanel.zombies[intCounter2].drawme();    
							} 
						for(int intCounter3 = 1;intCounter3 <= 9;intCounter3 = intCounter3 + 2){ // 5 harder
							gpanel.zombies[intCounter3] = new zombie("stronger",150,5);
							gpanel.zombies[intCounter3].drawme();    
						} 
						blnSpawned = true;
						}
					 	blnZombieStart = true; //makes sure that zombies aren't drawn before array is intialized
					 	  
			    	}else{ //After spawning, the zombies will start to move
					    dblTimer = dblTimer + 0.045;
						gpanel.zombies[0].move();
					    if(dblTimer > 2){ //Every second, a new zombie will start to move towards the house from spawn point
							gpanel.zombies[1].move();
					    }if(dblTimer > 3){
						    gpanel.zombies[2].move();
					    }if(dblTimer > 4){
						    gpanel.zombies[3].move();
					    }if(dblTimer > 6){
						    gpanel.zombies[4].move();
					    }if(dblTimer > 7){
						    gpanel.zombies[5].move();
					    }if(dblTimer > 8){
						    gpanel.zombies[6].move();
					    }if(dblTimer > 10){
						    gpanel.zombies[7].move();
					    }if(dblTimer > 11){
						    gpanel.zombies[8].move();
					    }if(dblTimer > 12){
						    gpanel.zombies[9].move();
					    }
			    	}
			    	
			    	//Only for Clients
			    	if(blnZombieStart){ //As long as zombies started moving after spawning
				    	if(blnServerMode){
					    	//Zombie Coord update by server
					    	//Server sends coords of zombies as long as blnAlive is true
					    	// Creating a super long string with ALL zobmie positions
					    	// Message Format
					    	// "ZCoords,0:300:350:,1:450:200:,
					    	String strZombieupdate = "ZCoords,"; 
				    		for(int intCounter = 0;intCounter < 10;intCounter++){
				    			strZombieupdate = strZombieupdate + intCounter + ":" + gpanel.zombies[intCounter].intX + ":" + gpanel.zombies[intCounter].intY + ",";
			    			}
			    			netcon.sendText(strZombieupdate);
			    			
			    			//Updates both Server and Client on the zombie's dead status boolean
							String strZombieupdate3 = "ZombieD1,"; 
							for(int intCounter = 0;intCounter < 10;intCounter++){
					    		strZombieupdate3 = strZombieupdate3 + intCounter + ":" + blnZombieDead[intCounter] + ",";
				    		}
				    		netcon.sendText(strZombieupdate3);
				    		
				    		//Updates the client on the wave # and the defense left in the house
				    		netcon.sendText("Waves,"+gpanel.intDefense+","+blnWaveDead+","+gpanel.intWave+","+intWaveWins);			    	
				    	}else if(blnClientMode){
					    	//Wave update from server
			    			String strReceive[] = textin.getText().split(",");
			    			if(strReceive[0].equalsIgnoreCase("Waves")){
					    		gpanel.intDefense = Integer.parseInt(strReceive[1]);
					    		blnWaveDead = Boolean.parseBoolean(strReceive[2]);
					    		gpanel.intWave = Integer.parseInt(strReceive[3]);
					    		intWaveWins = Integer.parseInt(strReceive[4]);
		    				}
				    	}	  	
				    	
				    	//ZOMBIE ATTACKING
					    	for(int intCounter = 0;intCounter < 10;intCounter++){ 
					    		if(gpanel.zombies[intCounter].intX > 225 && gpanel.zombies[intCounter].intX < 380 && gpanel.zombies[intCounter].intY == 255){
						    		if(gpanel.zombies[intCounter].intHP > 0){ //As long as zombies are not dead
							    		gpanel.zombies[intCounter].attack();
						    		}
						    	}	
					    	}
				    	//Checking for zombie dying
				    	for(int intCounter = 0;intCounter < 10; intCounter++){
					    	if(gpanel.zombies[intCounter].intHP <= 0){
						    	blnZombieDead[intCounter] = true;	
					    	}
				    	}
				    	//If all zombies in the wave are dead then the wavedead boolean is true
				    	if(blnZombieDead[0] == true && blnZombieDead[1] == true && blnZombieDead[2] == true && blnZombieDead[3] == true && blnZombieDead[4] == true &&
				    	blnZombieDead[5] == true && blnZombieDead[6] == true && blnZombieDead[7] == true && blnZombieDead[8] == true && blnZombieDead[9] == true){
					    	blnWaveDead = true;	
				    	}
				    	
				    	//FOR MOVING ON TO NEXT WAVE
				    	if(blnWaveDead == true){//If wave is dead
				    		if(gpanel.intWave < 9){
					    		if(blnSolo){ //Upgrade Screen only for solo mode!
					    			blnStart = false;
						    		ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/Upgrades.jpg")); //Sets the win image
									BG.setIcon(icon);
									//Cursor = Sets the cursor back to the beginning menu cursor
									Toolkit toolkit = Toolkit.getDefaultToolkit();
									Image images = toolkit.getImage(this.getClass().getResource("Players/customcursor.png")); //Imports the image
									Point hotSpot = new Point(0,0);  //Sets the reference point for the pointer on cursor
									Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Custom Cursor"); //Creates the new cursor
									setCursor(cursor); //Finally sets the cursor to replace current mouse cursor
									//Buttons to set visible
									nextwave.setVisible(true);
									Repair.setVisible(true);
									Upgrade.setVisible(true);
									if(blnBought1 == false){ //If player didn't buy gun then this will show
										Buy1.setVisible(true);
									}else{ //if gun is bought then equip button will show
										Equip[1].setVisible(true);
									}
									if(blnBought2 == false){
										Buy2.setVisible(true);
									}else{
										Equip[2].setVisible(true);
									}
									Equip[0].setVisible(true);
									
									//Sets the repair and upgrade buttons to enabled for now
									Repair.setEnabled(true);
									Upgrade.setEnabled(true);
									Buy1.setEnabled(true);
									Buy2.setEnabled(true);
									
									//Disables buttons if cash is insufficient
									if(gpanel.intDefense == (500 + (50*gpanel.intDefUpgrade)) || gpanel.intCash < 100){ //Makes sure that users can't repair after defense is full
										Repair.setEnabled(false);	
									}
									if(gpanel.intCash < 250){
										Buy1.setEnabled(false);
									}
									if(gpanel.intCash < 400){
										Buy2.setEnabled(false);
									}
									if(gpanel.intCash < 150){
										Upgrade.setEnabled(false);
									}
									
									gpanel.intWave++;
									blnZombieStart = false; //Makes sure the program doesn't read from an empty array before zombies are done spawning
									blnWaveDead = false;
									blnUpgrading = true; //For the draw commands at the bottom
    							
								}else{ //If multiplayer, then round proceeds
									for(int intCounter = 0;intCounter < 10;intCounter++){
										if(gpanel.zombies[intCounter].intHP <= 0){
						    				blnZombieDead[intCounter] = false;	
					    				}
					    			}
						    		blnSpawned = false;
						    		blnfileRead = false;
									dblTimer = 0; //The timer resets for the round
									blnZombieStart = false; //Makes sure the program doesn't read from an empty array before zombies are done spawning
									gpanel.intWave++;
									intWaveWins++; //Moves on to the next wave
									if(intWaveWins == 5){
										gpanel.intDefense = 600;	
									}else if(intWaveWins == 8){
										gpanel.intDefense = 700;	
									}	
									blnWaveDead = false;
								}	
				    		}	
				    	}
		    		}
				}
		    	//WIN CONDITION
		    	if(intWaveWins == 9){ //Only if all 8 waves are won
			    	blnAlive = false;
			    	ATimer.stop(); //Stops the ATimer to prevent any further game elements from running/refreshing
			    	ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/win.jpg")); //Sets the win image
					BG.setIcon(icon);
					blnStart = false; //game is now stopped
					main.setVisible(true); //Makes the main menu button appear
					
					//Cursor = Sets the cursor back to the beginning menu cursor
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Image images = toolkit.getImage(this.getClass().getResource("Players/customcursor.png")); //Imports the image
					Point hotSpot = new Point(0,0);  //Sets the reference point for the pointer on cursor
					Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Custom Cursor"); //Creates the new cursor
					setCursor(cursor); //Finally sets the cursor to replace current mouse cursor
		    	}
		    	//LOSE CONDITION
		    	if(gpanel.intDefense <= 0){
			    	blnAlive = false;
			    	//ATimer.stop();
			    	ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/Gameover.jpg"));
					BG.setIcon(icon);
					//blnStart = false;
					//blnZombieStart = false;
					blnLose = true;
					if(blnSolo == false){//If multiplayer then it sets up for exitting and chatting
						main.setText("Exit");	
						spane.setVisible(true);
						chat.setVisible(true);
					}else{ //If single player, it sets up to go back to main menu
						main.setText("Main Menu >>");	
					}
					main.setVisible(true);
					//Cursor = Sets the cursor back to the beginning menu cursor
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Image images = toolkit.getImage(this.getClass().getResource("Players/customcursor.png")); //Imports the image
					Point hotSpot = new Point(0,0);  //Sets the reference point for the pointer on cursor
					Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Custom Cursor"); //Creates the new cursor
					setCursor(cursor); //Finally sets the cursor to replace current mouse cursor
		    	}
		    	
			//Clear
			this.repaint(); //For animation
		}
		if(theEvent.getSource() == solo){ //SINGLE PLAYER MODE
			//When user presses the solo button, the Atimer will start and the buttons from before will not be visible anymore
			ATimer.start();
			solo.setVisible(false);
			team.setVisible(false);
			instructions.setVisible(false);
			blnSolo = true;
			blnStart = true;
			blnAlive = true;
			//Changes the background image to the start of game
			ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/background.jpg"));
			BG.setIcon(icon);
			//Changes cursor to a crosshair - red for P1
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image images = toolkit.getImage(this.getClass().getResource("Players/P1crosshair.png")); //Imports the new image
			Point hotSpot = new Point(12,12); //Sets the hotspot aka point of reference for cursor
			Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Crosshair P1"); //creates the new cursor
			setCursor(cursor); 
		}
		if(theEvent.getSource() == team){ //MULTIPLAYER MODE
			//When the multiplayer button is pressed, it sets certain buttons to false to hide them and makes others appear
			solo.setVisible(false);
			team.setVisible(false);
			instructions.setVisible(false);
			ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/multiplayer.jpg"));
			BG.setIcon(icon);
			Server.setVisible(true);
			Client.setVisible(true);
			IP.setVisible(true);
			port.setVisible(true);
			connect.setVisible(true);
			spane.setVisible(true);
			main.setVisible(true);
		}
		if(theEvent.getSource() == instructions){ //Goes back to main menu
			ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/Instructions.jpg"));
			BG.setIcon(icon);
			solo.setVisible(false);
			team.setVisible(false);
			instructions.setVisible(false);
			main.setVisible(true);
		}
		if(theEvent.getSource() == main){ //Goes back to main menu
			if(blnSolo == false && blnLose){
				System.exit(0);	
			}else{
				ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/mainmenu.jpg"));
				ATimer.stop();
				BG.setIcon(icon);
				main.setVisible(false);
				solo.setVisible(true);
				team.setVisible(true);
				instructions.setVisible(true);
				//Buttons for equipment
				Equip[0].setEnabled(false);
				Equip[0].setVisible(false);
				for(int intCounter = 1; intCounter < 3;intCounter++){
					Equip[intCounter].setEnabled(true);
					Equip[intCounter].setVisible(false);
				}
				reset();
			}
		}
		if(theEvent.getSource() == nextwave){ //Goes to the next wave
			//Cursor = Sets the cursor back to the beginning menu cursor
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image images = toolkit.getImage(this.getClass().getResource("Players/P1crosshair.png")); //Imports the image
			Point hotSpot = new Point(12,12);  //Sets the reference point for the pointer on cursor
			Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Custom Cursor"); //Creates the new cursor
			setCursor(cursor); //Finally sets the cursor to replace current mouse cursor
			
			//Changes the background image back to game
			ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/background.jpg"));
			BG.setIcon(icon);
			
			//Moves onto the next wave by setting the boolean to start
			dblTimer = 0; //The timer resets for the round
			blnStart = true;
			blnSpawned = false;
			blnUpgrading = false;
			
			//Buttons to set invisible
			nextwave.setVisible(false);
			Repair.setVisible(false);
			Upgrade.setVisible(false);
			Buy1.setVisible(false);
			Buy2.setVisible(false);
			for(int intCounter = 0;intCounter < 3;intCounter++){
				Equip[intCounter].setVisible(false);	
			}
			blnfileRead = false;
			gpanel.intKills = 0;//Resets the kills for each round
			
			for(int intCounter = 0;intCounter < 10;intCounter++){
				blnZombieDead[intCounter] = false;	
			}
			intWaveWins++; //Moves on to the next wave
		}
		if(theEvent.getSource() == Repair){ //Repairs the Defenses
			if(gpanel.intCash >= 100){ //If player has enough cash then this will happen
				startMusic("Sounds/upgrade.wav"); //Plays a sound for the upgrade screen
				gpanel.intCash = gpanel.intCash - 100;
				gpanel.intDefense = 500 + gpanel.intDefUpgrade;
				Repair.setEnabled(false);	
			}
			//If player doesn't have enough cash, these buttons will go disabled. Same with the other buttons
			if(gpanel.intCash < 250){
				Buy1.setEnabled(false);
			}
			if(gpanel.intCash < 400){
				Buy2.setEnabled(false);
			}
			if(gpanel.intCash < 150){
				Upgrade.setEnabled(false);
			}
		}
		if(theEvent.getSource() == Upgrade){ //Upgrades Defense
			if(gpanel.intCash >= 150){
				startMusic("Sounds/upgrade.wav"); //Plays a sound for the upgrade screen
				gpanel.intCash = gpanel.intCash - 150;
				gpanel.intDefense = gpanel.intDefense + 50;
				gpanel.intDefUpgrade++;
			}
			if(gpanel.intCash < 100){ 
				Repair.setEnabled(false);	
			}
			if(gpanel.intCash < 150){
				Upgrade.setEnabled(false);
			}
			if(gpanel.intCash < 250){
				Buy1.setEnabled(false);
			}
			if(gpanel.intCash  < 400){
				Buy2.setEnabled(false);
			}
		}
		if(theEvent.getSource() == Buy1){ //Buys Shotgun
			if(gpanel.intCash >= 250){
				startMusic("Sounds/upgrade.wav"); //Plays a sound for the upgrade screen
				Buy1.setVisible(false);
				Equip[1].setVisible(true);
				blnBought1 = true;
			}
			if(gpanel.intCash < 100){ 
				Repair.setEnabled(false);	
			}
			if(gpanel.intCash  < 400){
				Buy2.setEnabled(false);
			}
			if(gpanel.intCash < 150){
				Upgrade.setEnabled(false);
			}
		}
		if(theEvent.getSource() == Buy2){ //Buys Sniper
			if(gpanel.intCash >= 400){ 
				startMusic("Sounds/upgrade.wav"); //Plays a sound for the upgrade screen
				Buy2.setVisible(false);
				Equip[2].setVisible(true);
				blnBought2 = true;
			}
			if(gpanel.intCash < 100){ 
				Repair.setEnabled(false);	
			}
			if(gpanel.intCash < 250){
				Buy1.setEnabled(false);
			}
			if(gpanel.intCash < 150){
				Upgrade.setEnabled(false);
			}
		}
		if(theEvent.getSource() == Equip[0]){ //Equips magnum
			Equip[0].setEnabled(false);
			Equip[1].setEnabled(true);
			Equip[2].setEnabled(true);
			strGunType = "magnum";
			intGunDmg = 10;
			startMusic("Sounds/magnumreload.wav");
		}
		if(theEvent.getSource() == Equip[1]){ //Equips shotgun
			Equip[0].setEnabled(true);
			Equip[1].setEnabled(false);
			Equip[2].setEnabled(true);
			strGunType = "shotgun";
			intGunDmg = 30;
			startMusic("Sounds/shotgunreload.wav");
		}
		if(theEvent.getSource() == Equip[2]){ //Equips sniper
			Equip[0].setEnabled(true);
			Equip[1].setEnabled(true);
			Equip[2].setEnabled(false);
			strGunType = "sniper";
			intGunDmg = 50;
			startMusic("Sounds/sniperreload.wav");
		}
		if(theEvent.getSource() == Server){ //Server Button
			Server.setEnabled(false); 
			Client.setEnabled(true); //Makes client button enabled
			IP.setEnabled(false); //Server doesn't need IP therefore this is disabled
			blnServerMode = true;
			blnClientMode = false;
		}
		if(theEvent.getSource() == Client){ //Client Button
			Server.setEnabled(true);
			Client.setEnabled(false);
			IP.setEnabled(true);
			blnServerMode = false;
			blnClientMode = true;
		}
		if(theEvent.getSource() == connect){ //When user clicks connect, the program takes the info entered and tries to connect through network
			main.setVisible(false);
			intPort = Integer.parseInt(port.getText());
			if(blnServerMode == false && blnClientMode == false){
				opane1 = new JOptionPane(); //For when the user didn't select server or client yet
				opane1.showMessageDialog(this,"Please Select Client or Server Mode!","ERROR",opane1.WARNING_MESSAGE);
				main.setVisible(true);
			}else if(blnClientMode && IP.getText().equals("")){ //When user didn't enter IP
				opane1.showMessageDialog(this,"Please Enter an IP Address! Ask your Server for IP","ERROR",opane1.WARNING_MESSAGE);
				main.setVisible(true);
			}else{//If everything is entered fine then it starts to connect
				if(blnServerMode){//Server mode
					netcon = new SuperSocketMaster(textin, intPort);
					Thread netconthread = new Thread(netcon);
					netconthread.start();
				}else if(blnClientMode){//Client Mode
					netcon = new SuperSocketMaster(textin,IP.getText(), intPort);
					Thread netconthread = new Thread(netcon);
					netconthread.start();
				}
				connect.setEnabled(false);
			}
		}
		if(theEvent.getSource() == chat){ //FOR CHATTTING at gameover screen
			if(blnServerMode){
				netcon.sendText("Player 1: "+chat.getText());	
			}else if(blnClientMode){
				netcon.sendText("Player 2: "+chat.getText());
			}
			chat.setText("");	
		}
		if(theEvent.getSource() == textin){
			if(blnStart == false){ //as long as game isn't started
				ImageIcon icon = new ImageIcon(this.getClass().getResource("Game/background.jpg"));
				BG.setIcon(icon);
				Server.setVisible(false);
				Client.setVisible(false);
				IP.setVisible(false);
				port.setVisible(false);
				connect.setVisible(false);
				connections.setText("");
				spane.setVisible(false);
				blnSolo = false;
				blnStart = true;
				blnAlive = true;
				ATimer.start();
			}else if(blnStart && blnZombieStart){ //If game started
			//The following code is used for receiving data sent through the network
				if(blnServerMode){ //Only if serverMode
					//CURSOR
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Image images = toolkit.getImage(this.getClass().getResource("Players/P1crosshair.png"));
					Point hotSpot = new Point(12,12);  
					Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Crosshair P1"); 
					setCursor(cursor); 
					//Receiving mouse coords of player 2
		    		String strReceive4[] = textin.getText().split(",");
		    		if(strReceive4[0].equalsIgnoreCase("CoordinatesP2")){
			    		intMouseP2[0] = Integer.parseInt(strReceive4[1]);
			    		intMouseP2[1] = Integer.parseInt(strReceive4[2]);
		    		}			
		    		//Checks to see whether the other player shot or not
		    		String strReceive[] = textin.getText().split(",");
			    	if(strReceive[0].equalsIgnoreCase("SHOT2")){
				   		blnOtherPShot = Boolean.parseBoolean(strReceive[1]);
			   		}	 
			   		//ZOMBIE HEALTH UPDATE from client
			 	  	String strCommand;
			    	String strHealth;
			    	String strReceive2[];
			    	if(textin.getText().length() > 9){ //Logic Same as above
				    	strCommand = textin.getText().substring(0,8);
				    	if(strCommand.equals("ZHealth2")){
					    	strHealth = textin.getText().substring(9, textin.getText().length());
					    	strReceive = strHealth.split(",");
					    	for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
						    	strReceive2 = strReceive[intCounter2].split(":");
						    	gpanel.zombies[intCounter2].intHP = Integer.parseInt(strReceive2[1]);
					    	}
				    	}
					}				  
					//CHATTTING
					if(textin.getText().length() > 9){
						if(textin.getText().substring(0,8).equalsIgnoreCase("Player 2")){
							connections.append(textin.getText() + "\n");
						}
					}
					
				}else if(blnClientMode){ //Client mode receiving
					//Cursors again
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Image images = toolkit.getImage(this.getClass().getResource("Players/P2crosshair.png"));
					Point hotSpot = new Point(12,12);  
					Cursor cursor = toolkit.createCustomCursor(images, hotSpot, "Crosshair P2"); 
					setCursor(cursor);  
					//Mouse coords
		    		String strReceive5[] = textin.getText().split(",");
		    		if(strReceive5[0].equalsIgnoreCase("CoordinatesP1")){
			    		intMouseP1[0] = Integer.parseInt(strReceive5[1]);
			    		intMouseP1[1] = Integer.parseInt(strReceive5[2]);
		    		}
		    		//Shots from server
		    		String strReceive[] = textin.getText().split(",");
			    		if(strReceive[0].equalsIgnoreCase("SHOT1")){
				    		blnOtherPShot = Boolean.parseBoolean(strReceive[1]);
			    		}
			    	//Zombie health from server		
		    	  	String strCommand;
			    	String strHealth;
			    	String strReceive2[];
			    	if(textin.getText().length() > 8){
				    	strCommand = textin.getText().substring(0,7);
				    	if(strCommand.equals("ZHealth")){
					    	strHealth = textin.getText().substring(8, textin.getText().length());
					    	strReceive = strHealth.split(",");
					    	for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
						    	strReceive2 = strReceive[intCounter2].split(":");
						    	gpanel.zombies[intCounter2].intHP = Integer.parseInt(strReceive2[1]);
					    	}
				    	}
					}
					//Zombie coords
			    	String strCoords;
			    	//String strReceive[] = textin.getText().split(",");
			    	//System.out.println("received zombie coords");
			    	if(textin.getText().length() > 8){ //ONLY if the text length is greater than 8 so it won't substring a short string and crash
				    	strCommand = textin.getText().substring(0,7);
				    	if(strCommand.equals("ZCoords")){
					    	strCoords = textin.getText().substring(8, textin.getText().length());
					    	strReceive = strCoords.split(","); //Splits the text by commas into the array
					    	for(int intCounter = 0;intCounter < 10;intCounter++){
						    	strReceive2 = strReceive[intCounter].split(":");//Splits by colons into this array
						    	gpanel.zombies[intCounter].intX = Integer.parseInt(strReceive2[1]); //Finally sets the updated coords for client
						    	gpanel.zombies[intCounter].intY = Integer.parseInt(strReceive2[2]);
					    	}
				    	}
					}
					//Zombie dead boolean from server
			    	String strDead;				   	
				   	if(textin.getText().length() > 9){
					   	strCommand = textin.getText().substring(0,8);
					   	if(strCommand.equals("ZombieD1")){
					    	strDead = textin.getText().substring(9, textin.getText().length());
					    	strReceive = strDead.split(",");
					    	for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
						    	strReceive2 = strReceive[intCounter2].split(":");
						    	blnZombieDead[intCounter2] = Boolean.parseBoolean(strReceive2[1]);
					    	}
				    	}				
		    		}
		    		
		    		//CHATTTING
					if(textin.getText().length() > 9){
						if(textin.getText().substring(0,8).equalsIgnoreCase("Player 1")){
							connections.append(textin.getText()+ "\n");
						}
					}
				}
			}
		}
	}
	public void fileread(String strFileName){ //Method for reading from a text file
	    BufferedReader myfile = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(strFileName)));;
	    int intCounter = 0;   //InputStreamReader... etc is for Jars when getting the resource.
	  	
	    String strLine = "";
			try{
				strLine = myfile.readLine();
			}catch(IOException e){
	        	System.out.println("error reading from the file");
	      	}
	    while(strLine != null){
		     	strWaves[intCounter] = strLine;
		     	intCounter++;
			try{
				strLine = myfile.readLine();
			}catch(IOException e){
	        	System.out.println("error reading from the file");
	      	}
		}	
	    try{
	   		myfile.close();
	    }catch(IOException e){}	
    }
    
	//Methods required by KeyListener interface 
	public void keyReleased(KeyEvent theEvent){ //Whenever the key is let go these events will activate
		
	}
	
	public void keyPressed(KeyEvent theEvent){ //When a certain key is pressed down and held there
		
	}
	
	public void keyTyped(KeyEvent theEvent){ //When there is a single key press which reads from a single character
		
	}
	//Methods required by MouseListener interface 
	public void mouseExited(MouseEvent theEvent){ //When the mouse exits the window/game, then this will trigger.

	}

	public void mouseEntered(MouseEvent theEvent){ //When the mouse enters the game window, the instructions page will pop up

	}
	
	public void mouseReleased(MouseEvent theEvent){ // When the click is released then this is what will happen

	}
	
	public void mousePressed(MouseEvent theEvent){ // When mouse is pressed and not released yet then this will trigger
		if(blnStart){
			intMouseX1 = theEvent.getX();
			intMouseY1 = theEvent.getY();
			if(gpanel.intAmmo > 0 && blnZombieStart && blnDelaying == false){
				if(strGunType.equalsIgnoreCase("magnum")){
					startMusic("Sounds/magnum.wav");
				}else if(strGunType.equalsIgnoreCase("shotgun")){
					startMusic("Sounds/shotgun.wav");
				}else if(strGunType.equalsIgnoreCase("sniper")){
					startMusic("Sounds/sniper.wav");
				}
				
				blnShot = true;
				if(blnServerMode){
					netcon.sendText("SHOT1,"+blnShot);
				}else if(blnClientMode){
					netcon.sendText("SHOT2,"+blnShot);
				}
				if(strGunType.equals("shotgun")){ //If shotgun, it takes more ammo
					gpanel.intAmmo = gpanel.intAmmo - 2;
				}else{
					gpanel.intAmmo--;
				}
				for(int intCounter = 0;intCounter < 10;intCounter++){ //FOR NORMAL MOBS OF ZOMBIES
					if(strGunType.equals("shotgun")){//IF player is holding a shotgun
						//Zombie gets damaged more when shot near head
						//The following long if statement is for the 3 bullets shot out by shotgun
						if(intMouseX1 > gpanel.zombies[intCounter].intX - 12 && intMouseX1 < gpanel.zombies[intCounter].intX + 12 
						&& intMouseY1 > gpanel.zombies[intCounter].intY - 12 && intMouseY1 < gpanel.zombies[intCounter].intY + 12
						||intMouseX1 + 10> gpanel.zombies[intCounter].intX - 12 && intMouseX1 + 10 < gpanel.zombies[intCounter].intX + 12 
						&& intMouseY1 > gpanel.zombies[intCounter].intY - 12 && intMouseY1 < gpanel.zombies[intCounter].intY + 12
						|| intMouseX1 - 10> gpanel.zombies[intCounter].intX - 12 && intMouseX1 - 10 < gpanel.zombies[intCounter].intX + 12 
						&& intMouseY1 > gpanel.zombies[intCounter].intY - 12 && intMouseY1 < gpanel.zombies[intCounter].intY + 12){	
							
							gpanel.zombies[intCounter].intHP = gpanel.zombies[intCounter].intHP - (intGunDmg + 20);
							
						}else if(intMouseX1 > gpanel.zombies[intCounter].intX - 15 && intMouseX1 < gpanel.zombies[intCounter].intX + 20 
						&& intMouseY1 > gpanel.zombies[intCounter].intY + 5 && intMouseY1 < gpanel.zombies[intCounter].intY + 40
						||intMouseX1 + 10> gpanel.zombies[intCounter].intX - 12 && intMouseX1 + 10 < gpanel.zombies[intCounter].intX + 12 
						&& intMouseY1 > gpanel.zombies[intCounter].intY - 12 && intMouseY1 < gpanel.zombies[intCounter].intY + 12
						|| intMouseX1 - 10> gpanel.zombies[intCounter].intX - 12 && intMouseX1 - 10 < gpanel.zombies[intCounter].intX + 12 
						&& intMouseY1 > gpanel.zombies[intCounter].intY - 12 && intMouseY1 < gpanel.zombies[intCounter].intY + 12){	
							
							gpanel.zombies[intCounter].intHP = gpanel.zombies[intCounter].intHP - (intGunDmg + 10);

						}
					}else{
						//Zombie gets damaged more when shot near head
						if(intMouseX1 > gpanel.zombies[intCounter].intX - 12 && intMouseX1 < gpanel.zombies[intCounter].intX + 12 
						&& intMouseY1 > gpanel.zombies[intCounter].intY - 12 && intMouseY1 < gpanel.zombies[intCounter].intY + 12){	
							if(blnSolo){ //Does more damage in solo mode
								gpanel.zombies[intCounter].intHP = gpanel.zombies[intCounter].intHP - (intGunDmg + 20);
							}else{ //IF MULTIPLAYER - still the same
								gpanel.zombies[intCounter].intHP = gpanel.zombies[intCounter].intHP - (intGunDmg + 20);
							}
							if(blnServerMode){ //Sends the Zombie HP when they're shot
		 	 					//Sending 
								String strZombieupdate2 = "ZHealth,";	
								for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
									strZombieupdate2 = strZombieupdate2 + intCounter2 + ":" + gpanel.zombies[intCounter2].intHP + ",";
								}
						 		netcon.sendText(strZombieupdate2);//Sends an update of zombie HP from player 1 to player 2 		
						 		
	 			 			}else if(blnClientMode){
		 	 				 	//Sending 
								String strZombieupdate2 = "ZHealth2,";	
								for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
									strZombieupdate2 = strZombieupdate2 + intCounter2 + ":" + gpanel.zombies[intCounter2].intHP + ",";
						 		}
						 		netcon.sendText(strZombieupdate2);//Sends an update of zombie HP from player 2 to player 1
						 		
	  						}	 
						}
					}		
					//Zombie is dmged normally in body
					if(strGunType.equals("shotgun")){//IF player is holding a shotgun
						
					}else{
						if(intMouseX1 > gpanel.zombies[intCounter].intX - 15 && intMouseX1 < gpanel.zombies[intCounter].intX + 20 
						&& intMouseY1 > gpanel.zombies[intCounter].intY + 5 && intMouseY1 < gpanel.zombies[intCounter].intY + 40){
							if(blnSolo){
								gpanel.zombies[intCounter].intHP = gpanel.zombies[intCounter].intHP - (intGunDmg + 10);
							}else{ //IF MULTIPLAYER - Less damage
								gpanel.zombies[intCounter].intHP = gpanel.zombies[intCounter].intHP - (intGunDmg - 5);
							}
							
							if(blnServerMode){
		 	 					//Sending only
								String strZombieupdate2 = "ZHealth,";	
								for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
									strZombieupdate2 = strZombieupdate2 + intCounter2 + ":" + gpanel.zombies[intCounter2].intHP + ",";
								}
						 		netcon.sendText(strZombieupdate2);//Sends an update of zombie HP from player 1 to player 2
						 		
	 			 			}else if(blnClientMode){
		 	 				 	//Sending only
								String strZombieupdate2 = "ZHealth2,";	
								for(int intCounter2 = 0;intCounter2 < 10;intCounter2++){
									strZombieupdate2 = strZombieupdate2 + intCounter2 + ":" + gpanel.zombies[intCounter2].intHP + ",";
						 		}
						 		netcon.sendText(strZombieupdate2);//Sends an update of zombie HP from player 2 to player 1
	  						}	
						}
					}
					//Player gets kills if zombie dead
					if(gpanel.zombies[intCounter].intHP <= 0 && blnZombieDead[intCounter] == false){
						gpanel.intKills++;	//Player gets a kill pt
						if(blnSolo){
							gpanel.intCash = gpanel.intCash + (gpanel.intKills*(gpanel.intWave+1)); //Player gets cash according to wave # and kills
						}
					}
				}
				if(blnSolo){
					blnDelaying = true; //Used for gun delay so user cannot spam
				}
			}			
		}
	}
	
	public void mouseClicked(MouseEvent theEvent){ // When the mouse is clicked simply without holding, then this event will trigger
		
	}
	
	public void mouseMoved(MouseEvent theEvent){ // When the user moves the mouse, this will trigger.
	 	if(blnStart){ //When the game starts
	 		if(blnServerMode){ //Determines if you're the server or the client
	 			intMouseX1 = theEvent.getX(); //these 2 commands gets the X and Y coords of the mouse
				intMouseY1 = theEvent.getY();	
	 			netcon.sendText("CoordinatesP1,"+intMouseX1+","+intMouseY1); //Sends P1 mouse coords over the network
			}else if(blnClientMode){ //if client
	 			intMouseX2 = theEvent.getX(); 
				intMouseY2 = theEvent.getY();	
	 			netcon.sendText("CoordinatesP2,"+intMouseX2+","+intMouseY2); //Sends P2 mouse coords over the network
	 		}
	 	}
	}
	public void mouseDragged(MouseEvent theEvent){ //When the user holds onto the click and moves, it will trigger this event

	}
	//Start Music
	public AudioStream startMusic(String strFileName){
		InputStream Music = null;
		AudioStream Player = null;
		
		try{
		Music = this.getClass().getResourceAsStream(strFileName);
		Player = new AudioStream(Music);
		} catch (IOException e){
		}
		
		AudioPlayer.player.start(Player);
		return Player;
	}

	public void reset(){ //Resets everything back to it's initial values
		dblTimer = 0;
		
		blnStart = false;
		blnSolo = false;
		blnSpawned = false;
		blnfileRead = false;
		blnWaveDead = false;
		blnServerMode = false;
		blnZombieStart = false;
		blnClientMode = false;
		blnOtherPShot = false;
		blnShot = false;
		blnAlive = false;
		blnBought1 = false;
		blnBought2 = false;
		blnLose = false;
		
		for(int intCounter = 0;intCounter < 10;intCounter++){
			blnZombieDead[intCounter] = false;	
		}
		
		intWaveWins = 0;
		
		intGunDmg = 10;
		strGunType = "magnum";
		intDelay = 0;
		
		gpanel.intKills = 0;
		gpanel.intDefense = 500;
		gpanel.intAmmo = 4;
		gpanel.intWave = 0;
		gpanel.intReloadX = 0;
		gpanel.intCash = 0;
		gpanel.intDefUpgrade = 0;
		
		Server.setVisible(false);
		Client.setVisible(false);
		Server.setEnabled(true);
		Client.setEnabled(true);
		connect.setEnabled(true);
		IP.setEnabled(true);
		IP.setText("");
		
		IP.setVisible(false);
		port.setVisible(false);
		connect.setVisible(false);
		spane.setVisible(false);
	}
	public class JDBPanel extends JPanel{	
		//Image Variables
		BufferedImage zombie = null;
		BufferedImage zombie2 = null;
		BufferedImage zombie3 = null;
		BufferedImage zombie4 = null;
		BufferedImage zombiedead = null;
		BufferedImage zombie2dead = null;
		BufferedImage zombie3dead = null;
		BufferedImage zombie4dead = null;
		BufferedImage P1 = null;
		BufferedImage P2 = null;
		BufferedImage ammo = null;
		BufferedImage crossP1 = null;
		BufferedImage crossP2 = null;
		BufferedImage boss1 = null;
		BufferedImage boss2 = null;
		BufferedImage stats = null;
		
		//Others
		public zombie zombies[] = new zombie[10];
		public int intKills = 0;
		public int intDefense = 500;
		public int intAmmo = 4;
		public int intWave = 0;
		public int intReloadX = 0;
		public int intCash = 0; //Money for the game
		public int intDefUpgrade = 0;
		
		public JDBPanel(){
			super();	
			try{ //Importing Images
				zombie = ImageIO.read(this.getClass().getResource("Enemies/zombie.png"));
				zombie2 = ImageIO.read(this.getClass().getResource("Enemies/zombie2.png"));
				zombie3 = ImageIO.read(this.getClass().getResource("Enemies/zombie3.png"));
				zombie4 = ImageIO.read(this.getClass().getResource("Enemies/zombie4.png"));
				zombiedead = ImageIO.read(this.getClass().getResource("Enemies/zombiedead.png"));
				zombie2dead = ImageIO.read(this.getClass().getResource("Enemies/zombiedead2.png"));
				zombie3dead = ImageIO.read(this.getClass().getResource("Enemies/zombiedead3.png"));
				zombie4dead = ImageIO.read(this.getClass().getResource("Enemies/zombiedead4.png"));
				boss1 = ImageIO.read(this.getClass().getResource("Enemies/boss1.png"));
				boss2= ImageIO.read(this.getClass().getResource("Enemies/boss2.png"));
				P1 = ImageIO.read(this.getClass().getResource("Players/P1.png"));
				P2 = ImageIO.read(this.getClass().getResource("Players/P2.png"));	
				ammo = ImageIO.read(this.getClass().getResource("Game/Ammo.png"));
				stats = ImageIO.read(this.getClass().getResource("Game/statbar.jpg"));
				crossP1 = ImageIO.read(this.getClass().getResource("Players/P1crosshair.png"));
				crossP2 = ImageIO.read(this.getClass().getResource("Players/P2crosshair.png"));	
			}catch (IOException e) {} 
		}
		public void paintComponent(Graphics g){ //Paint Component
			super.paintComponents(g);
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		   		   
		    if(blnZombieStart && blnAlive){//Only starts printing out the zombies when the above array is done loading - prevents crash
				if(blnWaveDead == false){ //While the whole wave isn't dead
			    	for(int intCounter = 0;intCounter < 10;intCounter++){ //Prints different types of zombies depending on the variable strType
				    	if(zombies[intCounter].intHP > 0 && blnZombieDead[intCounter] == false){ //As long as the zombie isn't dead, the image will continue to draw
					    	if(zombies[intCounter].strType.equalsIgnoreCase("weak")){
								g2d.drawImage(zombie, zombies[intCounter].intX,zombies[intCounter].intY,null);
							}if(zombies[intCounter].strType.equalsIgnoreCase("medium")){
								g2d.drawImage(zombie2, zombies[intCounter].intX,zombies[intCounter].intY,null);
							}if(zombies[intCounter].strType.equalsIgnoreCase("strong")){
								g2d.drawImage(zombie3, zombies[intCounter].intX,zombies[intCounter].intY,null);
							}if(zombies[intCounter].strType.equalsIgnoreCase("stronger")){
								g2d.drawImage(zombie4, zombies[intCounter].intX,zombies[intCounter].intY,null);	
							}
						}else if(zombies[intCounter].intHP <= 0 && blnZombieDead[intCounter]){
							if(zombies[intCounter].strType.equalsIgnoreCase("weak")){
								g2d.drawImage(zombiedead, zombies[intCounter].intX,zombies[intCounter].intY,null);
 							}if(zombies[intCounter].strType.equalsIgnoreCase("medium")){
								g2d.drawImage(zombie2dead, zombies[intCounter].intX,zombies[intCounter].intY,null);
							}if(zombies[intCounter].strType.equalsIgnoreCase("strong")){
								g2d.drawImage(zombie3dead, zombies[intCounter].intX,zombies[intCounter].intY,null);
							}if(zombies[intCounter].strType.equalsIgnoreCase("stronger")){
								g2d.drawImage(zombie4dead, zombies[intCounter].intX,zombies[intCounter].intY,null);	
							}	
						}
					}
				}
	    	}
	    	//Sets color to white
		    g2d.setColor(Color.white);
	    	//UPGRADE STUFF
	    	if(blnSolo){//Feature only for solo mode
		    	if(blnUpgrading){//Only at upgrading Screen
			    	g2d.drawString("$"+intCash+"",120,375);
			    	g2d.drawString(intWave+"",140,400);
			    	g2d.drawString(intDefense+"",300,160);	
		    	}
	    	}
	    	if(blnStart && blnAlive){ //As soon as user chooses a button at the main menu this will start
	    		//Images
		    	g2d.drawImage(stats,0,0,null); //Stat bar at the top
		    	g2d.drawImage(P1,270,80,null); //Draws Player 1
		    	if(blnSolo == false){ // Only for multiplayer mode
			    	g2d.drawImage(P2,340,80,null); //Draws Player 2 only if its network mode
			    	//Draws the other player's crosshair - Decodes at the Textin
		    		if(blnServerMode){
			    		g2d.drawImage(crossP2,intMouseP2[0]-15,intMouseP2[1]-14,null);
		    		}else if(blnClientMode){	
			    		g2d.drawImage(crossP1,intMouseP1[0]-15,intMouseP1[1]-14,null);
		    		}
		    	}
		    	if(blnSolo){ //FOR SOLO ONLY
			    	g2d.drawString("$"+intCash+"",140,18); //Prints out the cash for solo player only	
		    	}
		    	//HOUSE HP
		    	g2d.drawString(intDefense+"",70,18); 
		    	//RELOAD TIME 
		    	g2d.drawRect(285,7,28,10); 
		    	//Labels
		    	g2d.drawString(intKills+"",470,18); //by adding "" to int, it converts it into a string
		    	g2d.drawString(intWave+"",380,18); //Therefore, being able to use the drawString command
		    	//Ammo and Shooting
		    	if(intAmmo == 4){
			    	g2d.drawImage(ammo,225,0,null); //Draws Ammo
			    	g2d.drawImage(ammo,240,0,null); //Draws Ammo
			    	g2d.drawImage(ammo,255,0,null); //Draws Ammo
			    	g2d.drawImage(ammo,270,0,null); //Draws Ammo
		    	}else if(intAmmo == 3){
			    	g2d.drawImage(ammo,225,0,null); //Draws Ammo
			    	g2d.drawImage(ammo,240,0,null); //Draws Ammo
			    	g2d.drawImage(ammo,255,0,null); //Draws Ammo
		    	}else if(intAmmo == 2){
			    	g2d.drawImage(ammo,225,0,null); //Draws Ammo
			    	g2d.drawImage(ammo,240,0,null); //Draws Ammo
		    	}else if(intAmmo == 1){
			    	g2d.drawImage(ammo,225,0,null); //Draws Ammo
		    	}
		    	//Draws the shots coming from the corresponding players
		    	if(blnShot == true && intAmmo >= 0){
			    	if(blnServerMode || blnSolo){ //Server is P1
			    		if(strGunType.equals("magnum") || strGunType.equals("sniper")){
					    	g2d.drawLine(280,100,intMouseX1,intMouseY1);
					    	blnShot = false;
			    		}else if(strGunType.equals("shotgun")){ //3 lines for shotguns
				    		g2d.drawLine(280,100,intMouseX1,intMouseY1); //Middle Line
				    		g2d.drawLine(280,100,intMouseX1 - 20,intMouseY1); //Left Line
				    		g2d.drawLine(280,100,intMouseX1 + 20,intMouseY1); //Right Line
				    		blnShot = false;
			    		}
		    		}else{ //Client is P2
			    		g2d.drawLine(350,100,intMouseX1,intMouseY1);
				    	blnShot = false;
		    		}
		    	}
		    	if(blnDelaying){ //DELAYINGGG
			    	intDelay++;
			    	if(strGunType.equalsIgnoreCase("magnum")){
				    	if(intDelay == 8){ //Delay of the gun
					    	intDelay = 0;
					    	blnDelaying = false;	
				    	}	
		    		}else if(strGunType.equalsIgnoreCase("shotgun")){
			    		if(intDelay == 15){ //Delay of the gun
					    	intDelay = 0;
					    	blnDelaying = false;	
				    	}
		    		}else if(strGunType.equalsIgnoreCase("sniper")){
			    		if(intDelay == 20){ //Delay of the gun
					    	intDelay = 0;
					    	blnDelaying = false;	
				    	}
		    		}
		    	}
		    	//Draws the shots coming from the other player
			    	if(blnServerMode){
			    		if(blnOtherPShot){
			    			g2d.drawLine(350,100,intMouseP2[0],intMouseP2[1]);
			    			blnOtherPShot = false;
		    			}
			    	}else if(blnClientMode){
			    		if(blnOtherPShot){
							g2d.drawLine(280,100,intMouseP1[0],intMouseP1[1]);
							blnOtherPShot = false;
		    			}
	    			}
		    	if(intAmmo == 0){ //If ammo is empty then it reloads
			    	g2d.setColor(Color.white);
			    	g2d.fillRect(285,7,intReloadX,10); //RELOAD TIME  - Different times for each type of gun
			    	if(strGunType.equalsIgnoreCase("magnum")){
			    		intReloadX = intReloadX + 4; //For the reload bar
			    	}else if(strGunType.equalsIgnoreCase("shotgun")){
			    		intReloadX = intReloadX + 2; //For the reload bar
		    		}else if(strGunType.equalsIgnoreCase("sniper")){
				    	intReloadX = intReloadX + 1; //For the reload bar	
		    		}
		    		if(intReloadX == 16){ //Plays reload sound in the middle of reloading
			    		if(strGunType.equalsIgnoreCase("magnum")){
					    	startMusic("Sounds/magnumreload.wav");
		    			}else if(strGunType.equalsIgnoreCase("shotgun")){
					   		startMusic("Sounds/shotgunreload.wav");
		    			}else if(strGunType.equalsIgnoreCase("sniper")){
					    	startMusic("Sounds/sniperreload.wav");
		    			}
	    			}
			    	if(intReloadX == 28){ //When it reaches 28 (full bar) ammo is reloaded
				    	intAmmo = 4; //RELOADED
				    	intReloadX = 0;	
				    	blnShot = false;
			    	}
		    	}
	    	}	
			g2d.dispose(); //For Animations	
	    	}
		}

// Z O M B I E S
// This is the blueprint file for zombies.
// The zombies will inherit the values below.

public class zombie{
	public String strType;
	public int intHP;
	public int intDmg;
	public int intX;
	public int intY;
	
	public zombie(){
		this.strType = "weak";
		this.intHP = 60;
		this.intDmg = 1;
	}
	
	public zombie(String strT, int intHealth, int intDamage){
		this.strType = strT;
		this.intHP = intHealth;
		this.intDmg = intDamage;
	}
	
	public void move(){ //The moving method for the zombies
		if(intHP > 0){ //Only if the zombie isn't dead it'll keep moving
			int intXTarget =(int)(Math.random()*155) + 230; //A random x value along the house to end up at
			int intXDistance = intX - intXTarget; //Will take the X distance away from house
			int intYDistance = intY - 255; //Will take the Y distance away from house
			
			if(intXDistance < 0){ //If negative number
				if(intX != intXTarget){ //as long as the X coordinate of zombie is not the target
					intX = intX + 2;	//Will make the zombie move right
				}	
			}else if(intXDistance > 0){ //If positive number
				if(intX != intXTarget){
					intX = intX - 2;	//Will make the zombie move left
				}		
			}		
			if(intY != 255){ //As long as the zombie isn't at the Y coordinate of house
				intY = intY - 3; //Continues to move	
			}
		}
	}
	
	public void drawme(){ //randomizes the spawning point of the zombies 
			this.intX = (int)(Math.random()*500); //Appears randomly on the X axis
			this.intY = 630; //set off the screen
	}
	public void attack(){
		gpanel.intDefense = gpanel.intDefense - intDmg; //For attacking the house, the damage is determined by this equation
	}
}
	public static void main(String[] args){ //Main
		AHERevenge zombies = new AHERevenge();
		zombies.setVisible(true); 
	}
}
package fr.paradoxal.launcher;

import static fr.theshark34.swinger.Swinger.drawFullsizedImage;
import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.getTransparentWhite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.swinger.animation.Animator;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {

	public static final File P_B_DIR = new File(GameDirGenerator.createGameDir("paradoxal"),"Launcher");
	public static File crack = new File(P_B_DIR,"crack.txt");
	public static File premiun = new File(P_B_DIR,"premiun.txt");
	
	private Image background = getResource("back.png");
	private Saver saver = new Saver(new File(InfoLauncher.P_DIR,"launcher.properties"));
	
	private JTextField usernamefield = new JTextField(this.saver.get("username"));
	private JPasswordField passwordField = new JPasswordField();
	
	private STexturedButton playbutton = new STexturedButton(getResource("play.png"));
	private STexturedButton exitbutton = new STexturedButton(getResource("exit.png"));
	private STexturedButton reducebutton = new STexturedButton(getResource("reduce.png"));
	
	private STexturedButton rambutton  = new STexturedButton(getResource("settings.png"));

	
	private SColoredBar progressBar = new SColoredBar(getTransparentWhite(100),getTransparentWhite(175));
	
	private JLabel infolabel = new JLabel("Clique sur Fight ! ",SwingConstants.CENTER);

	
	public LauncherPanel() {
		this.setLayout(null);
		
		usernamefield.setForeground(Color.BLACK);
		usernamefield.setFont(usernamefield.getFont().deriveFont(19F));
		usernamefield.setCaretColor(Color.BLACK);
		usernamefield.setOpaque(false);
		usernamefield.setBorder(null);
		usernamefield.setBounds(715, 233, 238, 25);
		this.add(usernamefield);
		
		passwordField.setForeground(Color.BLACK);
		passwordField.setFont(passwordField.getFont().deriveFont(19F));
		passwordField.setCaretColor(Color.BLACK);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(715, 345, 238, 25);
		this.add(passwordField);
		
		playbutton.setBounds(740, 400);
		playbutton.addEventListener(this);
		this.add(playbutton);
		
		exitbutton.setBounds(920, 10, 35, 35);
		exitbutton.addEventListener(this);
		this.add(exitbutton);
		
		reducebutton.setBounds(880,10,35,35);
		reducebutton.addEventListener(this);
		this.add(reducebutton);
		
		rambutton.setBounds(840, 10, 35, 35);
		rambutton.addEventListener(this);
		this.add(rambutton);
		
		progressBar.setBounds(12,590,950,25);
		this.add(progressBar);
		
		infolabel.setForeground(Color.white);
		infolabel.setFont(usernamefield.getFont());
		infolabel.setBounds(12,550,951,25);
		this.add(infolabel);
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEvent(SwingerEvent arg0) {
		if(arg0.getSource()==playbutton)
		{
			setFieldEnabled(false);
			if(usernamefield.getText().replaceAll(" ", "").length()==0 || passwordField.getText().length()==0)
			{
				JOptionPane.showMessageDialog(this, "Erreur, veuilliez entrée un Pseudo et un mot de passe valides", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldEnabled(true);
				return;
			}
			Thread t = new Thread() 
			{
				@Override
				public void run() {
					try {
						Launcher.auth(usernamefield.getText(), passwordField.getText());
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, imposible de ce connecter: "+e.getErrorModel().getErrorMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						setFieldEnabled(true);
						return;
					}
					saver.set("username",usernamefield.getText());
					try {
						Launcher.update();
					} catch (Exception e) {
						Launcher.interruptThread();
						Launcher.getCrashReporter().catchError(e, "Impossible de mettre à jour Paradoxal !");			
						setFieldEnabled(true);
						return;
					}
					try {
						Launcher.launch();
					}catch (LaunchException e) {
						Launcher.getCrashReporter().catchError(e, "Impossible de lancher Paradoxal");
						setFieldEnabled(true);
						return;
					}
				}
			};
			t.start();
		}
		else if(arg0.getSource()==exitbutton)
		{
			Animator.fadeOutFrame(Launcher.getinstance(),new Runnable() 
			{
				
				@Override
				public void run() {
					System.exit(0);
				}
			});
		}
		else if(arg0.getSource()==rambutton) 
		{
			Launcher.ramSelector.setFrameClass(BootstrapRamSelecor.class);
			Launcher.ramSelector.display();
		}
		else if(arg0.getSource()==reducebutton)
		{
			Launcher.getinstance().setState(JFrame.ICONIFIED);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		drawFullsizedImage(g, this, background);
	}
	
	private void setFieldEnabled(boolean enabled) 
	{
		usernamefield.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playbutton.setEnabled(enabled);
	}
	
	public SColoredBar getprogressbar() 
	{
		return progressBar;
	}
	
	public void setInfoLabel(String text)
	{
		infolabel.setText(text);
	}

}

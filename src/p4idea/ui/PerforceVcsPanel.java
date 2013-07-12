package p4idea.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServerInfo;
import p4idea.P4Logger;
import p4idea.perforce.P4Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PerforceVcsPanel
{
  private final Project _project;
  private JPanel _rootPanel;
  private JTextField _p4port;
  private JTextField _p4user;
  private JTextField _p4client;
  private JButton _testButton;

  public PerforceVcsPanel( Project project )
  {
    _project = project;
    _testButton.addActionListener( new TestConnectionListener() );
  }

  public void initialize()
  {
    P4Settings settings = _project.getComponent( P4Settings.class );
    _p4port.setText( settings.getP4port() );
    _p4user.setText( settings.getP4user() );
    _p4client.setText( settings.getP4client() );
  }

  public JComponent getPanel()
  {
    return _rootPanel;
  }

  public boolean isModified()
  {
    P4Settings settings = _project.getComponent( P4Settings.class );

    boolean unmodified = _p4port.getText().equals( settings.getP4port() );
    unmodified &= _p4user.getText().equals( settings.getP4user() );
    unmodified &= _p4client.getText().equals( settings.getP4client() );
    return !unmodified;
  }

  public void apply( P4Settings settings ) throws ConfigurationException
  {
    apply( settings, false );
  }

  private void apply( P4Settings settings, boolean verbose ) throws ConfigurationException
  {
    P4Settings current = P4Settings.clone( settings );

    settings.setP4port( _p4port.getText() );
    settings.setP4user( _p4user.getText() );
    settings.setP4client( _p4client.getText() );
    try
    {
      testSettings( settings, verbose );
      settings.apply();
    }
    catch ( ConfigurationException e )
    {
      settings.loadState( current );
      throw e;
    }
  }

  private void testSettings( P4Settings settings, boolean verbose ) throws ConfigurationException
  {
    if ( settings.isUnset() )
    {
      final String title = "Error Connecting to Perforce";
      final String error = String.format( "Incomplete Perforce Settings: %s", settings );
      Messages.showErrorDialog( error, title );
      throw new ConfigurationException( error );
    }

    try
    {
      try
      {
        IServerInfo info = settings.verify();
        if ( verbose )
        {
          UserInput.displayPerforceInfo( _rootPanel, info );
        }
      }
      catch ( AccessException ae )
      {
        IServerInfo info = UserInput.requestCredentials( settings );
        if ( null != info )
        {
          UserInput.displayPerforceInfo( _rootPanel, info );
        }
        else
        {
          throw ae;
        }
      }
    }
    catch ( AccessException | ConnectionException | RequestException e )
    {
      final String msg = "Error Connecting to Perforce";
      P4Logger.getInstance().error( msg, e );
      Messages.showErrorDialog( e.getMessage(), msg );
      throw new ConfigurationException( msg );
    }
  }

  private class TestConnectionListener implements ActionListener
  {
    @Override
    public void actionPerformed( ActionEvent event )
    {
      P4Settings testSettings = new P4Settings();
      try
      {
        apply( testSettings, true );
      }
      catch ( ConfigurationException e )
      {
        // Already logged
      }
    }
  }
}

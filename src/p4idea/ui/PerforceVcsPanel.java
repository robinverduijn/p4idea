package p4idea.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
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
  private JTextArea _status;

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
    setStatus();
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
    P4Settings current = P4Settings.clone( settings );

    settings.setP4port( _p4port.getText() );
    settings.setP4user( _p4user.getText() );
    settings.setP4client( _p4client.getText() );
    try
    {
      testSettings( settings );
      settings.apply();
    }
    catch ( ConfigurationException e )
    {
      settings.loadState( current );
      throw e;
    }
  }

  private void testSettings( P4Settings settings ) throws ConfigurationException
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
        setStatus( info );
      }
      catch ( AccessException ae )
      {
        IServerInfo info = UserInput.requestCredentials( settings );
        setStatus( info );
        if ( null == info )
        {
          throw ae;
        }
      }
    }
    catch ( AccessException | ConnectionException | RequestException e )
    {
      throw new ConfigurationException( "Error Connecting to Perforce" );
    }
  }

  private void setStatus()
  {
    P4Settings settings = _project.getComponent( P4Settings.class );
    settings.setP4port( _p4port.getText() );
    settings.setP4user( _p4user.getText() );
    settings.setP4client( _p4client.getText() );

    IServerInfo info = null;
    try
    {
      info = settings.verify();
    }
    catch ( ConnectionException | AccessException | RequestException e )
    {
      P4Logger.getInstance().error( "Invalid P4 settings", e );
    }
    setStatus( info );
  }

  private void setStatus( IServerInfo info )
  {
    if ( null != info )
    {
      StringBuilder sb = new StringBuilder();
      for ( String line : UserInput.getPerforceInfo( info ) )
      {
        sb.append( line ).append( "\n" );
      }
      _status.setForeground( JBColor.DARK_GRAY );
      _status.setText( sb.toString() );
    }
    else
    {
      _status.setForeground( JBColor.RED );
      _status.setText( "Perforce settings are invalid.\nPlease login or double-check your server settings." );
    }
  }

  private class TestConnectionListener implements ActionListener
  {
    @Override
    public void actionPerformed( ActionEvent event )
    {
      try
      {
        apply( new P4Settings() );
      }
      catch ( ConfigurationException e )
      {
        // Already logged
      }
    }
  }
}

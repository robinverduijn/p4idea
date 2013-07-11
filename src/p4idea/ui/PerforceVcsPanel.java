package p4idea.ui;

import com.intellij.openapi.ui.Messages;
import com.perforce.p4java.exception.*;
import com.perforce.p4java.server.IServerInfo;
import p4idea.P4Logger;
import p4idea.PerforcePlugin;
import p4idea.perforce.P4Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PerforceVcsPanel
{
  private JPanel _rootPanel;
  private JTextField _p4port;
  private JTextField _p4user;
  private JTextField _p4client;
  private JButton _testButton;

  public PerforceVcsPanel()
  {
    initialize();
    _testButton.addActionListener( new TestConnectionListener() );
  }

  public void initialize()
  {
    P4Settings settings = PerforcePlugin.getInstance().getState();
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
    P4Settings settings = PerforcePlugin.getInstance().getState();

    boolean unmodified = _p4port.getText().equals( settings.getP4port() ) && _p4user.getText().equals( settings
        .getP4user() ) && _p4client.getText().equals( settings.getP4client() );
    return !unmodified;
  }

  public void apply( P4Settings settings )
  {
    settings.setP4port( _p4port.getText() );
    settings.setP4user( _p4user.getText() );
    settings.setP4client( _p4client.getText() );
  }

  private class TestConnectionListener implements ActionListener
  {
    @Override
    public void actionPerformed( ActionEvent event )
    {
      P4Settings testSettings = new P4Settings();
      apply( testSettings );

      if ( testSettings.isUnset() )
      {
        final String title = "Error Connecting to Perforce";
        final String error = String.format( "Incomplete Perforce Settings: %s", testSettings );
        Messages.showErrorDialog( error, title );
        return;
      }

      try
      {
        try
        {
          IServerInfo info = testSettings.verify();
          UserInput.getInstance().displayPerforceInfo( _rootPanel, info );
        }
        catch ( AccessException ae )
        {
          IServerInfo info = UserInput.getInstance().requestCredentials( testSettings );
          if ( null != info )
          {
            UserInput.getInstance().displayPerforceInfo( _rootPanel, info );
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
      }
    }
  }
}

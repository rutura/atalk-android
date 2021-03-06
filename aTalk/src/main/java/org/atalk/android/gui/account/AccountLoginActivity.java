/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.atalk.android.gui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;

import net.java.sip.communicator.service.gui.AccountRegistrationWizard;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.Logger;

import org.atalk.android.*;
import org.atalk.android.gui.aTalk;
import org.atalk.android.gui.menu.ExitMenuActivity;
import org.atalk.android.gui.util.AndroidUtils;
import org.osgi.framework.*;

import java.util.Map;

/**
 * The <tt>AccountLoginActivity</tt> is the activity responsible for creating or
 * registration a new account on the server.
 *
 * @author Yana Stamcheva
 * @author Pawel Domas
 * @author Eng Chong Meng
 */
public class AccountLoginActivity extends ExitMenuActivity
		implements AccountLoginFragment.AccountLoginListener
{
	Logger logger = Logger.getLogger(AccountLoginActivity.class);

	/**
	 * The username property name.
	 */
	public static final String USERNAME = "Username";

	/**
	 * The password property name.
	 */
	public static final String PASSWORD = "Password";

	/**
	 * Called when the activity is starting. Initializes the corresponding call interface.
	 *
	 * @param savedInstanceState
	 * 		If the activity is being re-initialized after previously being shut down then this
	 * 		Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
	 * 		Note: Otherwise it is null.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// If we have instance state it means the fragment is already created
		if (savedInstanceState == null) {
			// Create AccountLoginFragment fragment
			String login = getIntent().getStringExtra(USERNAME);
			String password = getIntent().getStringExtra(PASSWORD);
			AccountLoginFragment accountLogin
					= AccountLoginFragment.createInstance(login, password);

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, accountLogin).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (BuildConfig.DEBUG) {
			menu.add(Menu.NONE, R.id.del_database, Menu.NONE, R.string.service_gui_PURGE_SQL_DB);
		}

		// Adds exit option from super class
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Create an new account database with the given <tt>userName</tt>, <tt>password</tt>
	 * and <tt>protocolName</tt>.
	 *
	 * @param userName
	 * 		the username of the account
	 * @param password
	 * 		the password of the account
	 * @param protocolName
	 * 		the name of the protocol
	 * @return the <tt>ProtocolProviderService</tt> corresponding to the newly signed in account
	 */
	private ProtocolProviderService createAccount(String userName, String password,
			String protocolName, Map<String, String> accountProperties)
	{
		BundleContext bundleContext = getBundleContext();
		// Find all the available AccountRegistrationWizard that the system has implemented
		ServiceReference<?>[] accountWizardRefs = null;
		try {
			accountWizardRefs = bundleContext
					.getServiceReferences(AccountRegistrationWizard.class.getName(), null);
		}
		catch (InvalidSyntaxException ex) {
			// this shouldn't happen since we have provided all parameter string
			logger.error("Error while retrieving service refs", ex);
		}

		// in case we found none, then exit.
		if (accountWizardRefs == null) {
			logger.error("No registered account registration wizards found");
			return null;
		}

		if (logger.isDebugEnabled())
			logger.debug("Found " + accountWizardRefs.length + " already installed providers.");

		// Get the user selected AccountRegistrationWizard for account registration
		AccountRegistrationWizard selectedWizard = null;
		for (ServiceReference<?> accountWizardRef : accountWizardRefs) {
			AccountRegistrationWizard accReg
					= (AccountRegistrationWizard) bundleContext.getService(accountWizardRef);
			if (accReg.getProtocolName().equals(protocolName)) {
				selectedWizard = accReg;
				break;
			}
		}
		if (selectedWizard == null) {
			logger.warn("No account registration wizard found for protocol name: " + protocolName);
			return null;
		}
		try {
			selectedWizard.setModification(false);
			return selectedWizard.signin(userName, password, accountProperties);
		}
		catch (OperationFailedException e) {
			logger.error("Account creation operation failed.", e);

			switch (e.getErrorCode()) {
				case OperationFailedException.ILLEGAL_ARGUMENT:
					AndroidUtils.showAlertDialog(this, R.string.service_gui_LOGIN_FAILED,
							R.string.service_gui_USERNAME_NULL);
					break;
				case OperationFailedException.IDENTIFICATION_CONFLICT:
					AndroidUtils.showAlertDialog(this, R.string.service_gui_LOGIN_FAILED,
							R.string.service_gui_USER_EXISTS_ERROR);
					break;
				case OperationFailedException.SERVER_NOT_SPECIFIED:
					AndroidUtils.showAlertDialog(this, R.string.service_gui_LOGIN_FAILED,
							R.string.service_gui_SPECIFY_SERVER);
					break;
				default:
					AndroidUtils.showAlertDialog(this, getString(R.string.service_gui_LOGIN_FAILED),
							getString(R.string.service_gui_ACCOUNT_CREATION_FAILED,
									e.getMessage()));
			}
		}
		catch (Exception e) {
			logger.error("Exception while adding account: " + e.getMessage(), e);
			AndroidUtils.showAlertDialog(this, getString(R.string.service_gui_ERROR),
					getString(R.string.service_gui_ACCOUNT_CREATION_FAILED, e.getMessage()));
		}
		return null;
	}

	/**
	 * See {@link AccountLoginFragment.AccountLoginListener#onLoginPerformed}
	 */
	@Override
	public void onLoginPerformed(String userName, String password, String network,
			Map<String, String> accountProperties)
	{
		ProtocolProviderService pps = createAccount(userName, password, network, accountProperties);

		if (pps != null) {
			Intent showContactsIntent = new Intent(aTalk.ACTION_SHOW_CONTACTS);
			startActivity(showContactsIntent);
			finish();
		}
	}
}
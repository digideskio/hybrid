package org.vaadin.hybrid;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.hybrid.backend.AddressbookBackend;
import org.vaadin.hybrid.backend.DummyAddressbookBackendImpl;
import org.vaadin.hybrid.serversideexample.AddressbookEditor;

import java.io.*;

@Theme("hybrid")
@SuppressWarnings("serial")
public class HybridUI extends UI {

    final VerticalLayout mainLayout = new VerticalLayout();
	final HorizontalLayout buttonBar = new HorizontalLayout();
	final HorizontalLayout exampleLayout = new HorizontalLayout();
	final Label exampleDescription = new Label("");
	Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {

		initNavigation();
		initLayout();
	}

	public AddressbookBackend getAddressBookService() {
		return DummyAddressbookBackendImpl
				.getAddressBookService(((WrappedHttpSession) getSession()
                        .getSession()).getHttpSession());
	}

	private void initLayout() {
		setContent(mainLayout);

		mainLayout.addComponent(buttonBar);
		mainLayout.addComponent(exampleLayout);

		buttonBar.setWidth("100%");

		exampleLayout.setWidth("100%");
		exampleLayout.setMargin(true);
		exampleLayout.setSpacing(true);
		HomeView firstView = new HomeView();
		exampleLayout.addComponent(firstView);
		exampleLayout.setExpandRatio(firstView, 1);
		exampleLayout.addComponent(exampleDescription);

		exampleDescription.setWidth("300px");
		exampleDescription.setContentMode(ContentMode.HTML);

	}

	private void initNavigation() {

		navigator = new Navigator(this, new ViewDisplay() {
			public void showView(View view) {
				exampleLayout.replaceComponent(exampleLayout.getComponent(0),
						(Component) view);
				exampleLayout.setExpandRatio((Component) view, 1);

				showDescription(view.getClass());
			}
		});

		navigator.addView("", HomeView.class);
		addView("server", "Server-side", AddressbookEditor.class);
		addView("gwtrpc", "GWT-RPC",
        org.vaadin.hybrid.gwtrpcexample.AddressbookView.class);
		addView("connector", "Connector",
                org.vaadin.hybrid.connectorexample.AddressbookView.class);
		addView("offline",
				"Offline",
                org.vaadin.hybrid.offlineexample.AddressbookView.class);
	}

	private void showDescription(Class<? extends View> viewClass) {
		InputStream is = getClass().getResourceAsStream(
				"descriptions/" + viewClass.getName() + ".html");
		if (is == null) {
			exampleDescription.setValue("");
			return;
		}
		final char[] buffer = new char[1024];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ignored) {
		} catch (IOException ignored) {
		}
		exampleDescription.setValue(out.toString());
	}

	private void addView(final String viewName, final String buttonCaption,
			final Class<? extends View> viewClass) {
		navigator.addView(viewName, viewClass);
		NativeButton button = new NativeButton(buttonCaption,
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						navigator.navigateTo(viewName);
					}
				});
		buttonBar.addComponent(button);
		button.setWidth("100%");
		button.addStyleName("menubutton");
	}
}

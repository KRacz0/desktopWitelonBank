package com.kracz0.desktopwitelonbank.Models;

import com.kracz0.desktopwitelonbank.Views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;

    private User loggedUser;

    private Model() {
        this.viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public String getAuthToken() {
        return loggedUser != null ? loggedUser.getToken() : null;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public void logout() {
        this.loggedUser = null;
    }
}

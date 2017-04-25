package ipserver;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class IpServer {
    public static void main(String[] args) {
        int intValue = 0;
        //ObservableValue<Integer> obsInt = new SimpleIntegerProperty(intValue).asObject();
        ObservableValue<Integer> obsInt = new ObservableValue<Integer>() {
            @Override
            public void addListener(InvalidationListener listener) {

            }

            @Override
            public void removeListener(InvalidationListener listener) {

            }

            @Override
            public void addListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public void removeListener(ChangeListener<? super Integer> listener) {

            }

            @Override
            public Integer getValue() {
                return null;
            }
        };
        obsInt.addListener((observable, oldValue, newValue) -> {
            System.out.print("kek");
        });
        obsInt.addListener((observable, oldValue, newValue) -> {
            System.out.print("lol");
        });
        intValue = 1;
        intValue = 2;
        intValue = 3;
        System.out.print(obsInt.getValue());
    }

}


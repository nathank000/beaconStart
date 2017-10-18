package com.example.m50571.beaconstart;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by m50571 on 10/16/17.
 */

public class EthResult {


    private String _timestamp;
    private Double _ethPrice;
    private Double _initialInvestment;
    private Double _ethAmount;
    private Double _profit;


    public EthResult(double ethPrice, double initInvestment, double ethAmount) {
        int time = (int) (System.currentTimeMillis());
        Timestamp tsTemp = new Timestamp(time);

        _timestamp = tsTemp.toString();
        _ethAmount = ethAmount;
        _initialInvestment = initInvestment;
        _ethPrice = ethPrice;

        _profit = calcProfit();

    }


    private double calcProfit() {
        return (_ethPrice * _ethAmount) - _initialInvestment;
    }


    public Double get_profit() {
        return _profit;
    }

    public void set_profit(Double _profit) {
        this._profit = _profit;
    }

    public String get_timestamp() {
        return _timestamp;
    }

    public void set_timestamp(String _timestamp) {
        this._timestamp = _timestamp;
    }

    public Double get_ethPrice() {
        return _ethPrice;
    }

    public void set_ethPrice(Double _ethPrice) {
        this._ethPrice = _ethPrice;
    }

    public Double get_initialInvestment() {
        return _initialInvestment;
    }

    public void set_initialInvestment(Double _initialInvestment) {
        this._initialInvestment = _initialInvestment;
    }

    public Double get_ethAmount() {
        return _ethAmount;
    }

    public void set_ethAmount(Double _ethAmount) {
        this._ethAmount = _ethAmount;
    }
}
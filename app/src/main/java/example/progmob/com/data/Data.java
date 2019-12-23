package example.progmob.com.data;

import android.widget.Button;

public class Data {
    private String id, nama, username, password, no_hp, alamat, kelamin, keterangan, harga, status, gambar, jumlah, total, tglTransaksi, tglAmbil, idTransaksi, idUser;
    private Button plus, minus;
    private int counter;


    public Data() {
    }

    public Data(String id, String nama, String alamat, String username, String password, String no_hp, String kelamin, String keterangan, String harga, String status, Button plus, Button minus, String gambar, String jumlah, String total, String tglTransaksi, String tglAmbil, String idTransaksi, String idUser) {
        this.id = id;
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.no_hp = no_hp;
        this.alamat = alamat;
        this.kelamin = kelamin;
        this.keterangan = keterangan;
        this.harga = harga;
        this.status = status;
        this.plus = plus;
        this.minus = minus;
        this.gambar = gambar;
        this.jumlah = jumlah;
        this.total = total;
        this.tglTransaksi = tglTransaksi;
        this.tglAmbil = tglAmbil;
        this.idTransaksi = idTransaksi;
        this.idUser = idUser;


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUsername() {
        return username;
    }

    public  void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public  void setPassword(String password) {
        this.password = password;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String alamat) {
        this.kelamin = kelamin;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public  String getStatus(){
        return  status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public  Button getPlus(){
        return plus;
    }

    public void setPlus(Button plus){
        this.plus = plus;

    }

    public  Button getMinus(){
        return minus;
    }

    public  void setMinus(Button minus){
        this.minus = minus;
    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int counter){
        this.counter = counter;
    }
    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(String tglTransaksi) {
        this.tglTransaksi = tglTransaksi;
    }

    public String getTglAmbil() {
        return tglAmbil;
    }

    public void setTglAmbil(String tglAmbil) {
        this.tglAmbil = tglAmbil;
    }

    public String getIdTransaksi(){
        return idTransaksi;
    }

    public  void setIdTransaksi(String idTransaksi){
        this.idTransaksi = idTransaksi;
    }

    public String getIdUser(){
        return idUser;
    }

    public void setIdUser(String idUser){
        this.idUser = idUser;
    }


}
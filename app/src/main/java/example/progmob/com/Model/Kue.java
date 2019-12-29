package example.progmob.com.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "tb_kue") //membuat table dengan nama tb_kue

public class Kue implements Serializable {

    //Membuat Kolom pada tabel
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private
    Integer id;

    @ColumnInfo(name = "nama")
    private
    String nama;

    @ColumnInfo(name = "keterangan")
    private
    String keterangan;

    @ColumnInfo(name = "harga")
    private
    String harga;

    @ColumnInfo(name = "gambar")
    private
    String gambar;

    @ColumnInfo(name = "status")
    private
    String status;

    @NonNull
    public Integer getId(){
        return id;
    }

    public void setId(@NonNull Integer id){
        this.id = id;
    }

    public String getNama(){
        return nama;
    }

    public void setNama(@NonNull String nama){
        this.nama = nama;
    }

    public String getKeterangan(){
        return keterangan;
    }

    public void setKeterangan(@NonNull String keterangan){
        this.keterangan = keterangan;
    }

    public String getHarga(){
        return harga;
    }

    public void setHarga(@NonNull String harga){
        this.harga = harga;
    }

    public String getGambar(){
        return gambar;
    }

    public void setGambar(@NonNull String gambar){
        this.gambar = gambar;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(@NonNull String status){
        this.status = status;
    }
}

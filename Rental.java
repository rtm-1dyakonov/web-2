import java.io.*;

// обьявление класса, он реализовывает интерфейс Serializable
public class Rental implements Serializable {
    // обявление скрытых полей класса
    private int id; // id
    private String point; // точка проката
    private Car []list; // массив обьектов
    private int length; // размер массива
    private int maxSize; // максимальное количество обьектов в массиве
    private static final long serialVersionUID = 6529685098267757690L;
    // конструктор с параметрами
    public Rental(String point, int maxSize){
        length = 0;
        this.point = point;
        this.maxSize = maxSize;
        list = new Car[maxSize];
    }
    // метод для получения автомобиля по индексу
    public Car getCar(int i) {
        return list[i];
    }

    // метод для получения id
    public int getId() {
        return id;
    }

    // метод для установки значения id
    public void setId(int id) {
        this.id = id;
    }

    // методы для получения значений полей
    public String getPoint() {
        return point;
    }

    public int getLength() {
        return length;
    }
    // метод добавления обьекта в конец массива
    public void add(Car car)
    {
        list[length++] = car;
    }
    // метод удаления обекта из массива по индексу
    public void delete(int index)
    {
        for(int i=--index; i<length-1;i++)
        {
            list[i] = list[i+1];
        }
        length--;
    }
    // метод сортировки по стоимости автомобилей
    public void sort()
    {
        for(int i=0;i<length;i++)
        {
            for(int j=0;j<length-1;j++)
            {
                if(list[j].getPrice() > list[j+1].getPrice())
                {
                    Car tmp = list[j];
                    list[j]=list[j+1];
                    list[j+1] = tmp;
                }
            }
        }
    }
    // метод для записи списка обьектов в файл с помощью сериализации
    public void write(String filename)
    {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))
        {
            for(int i=0;i<length;i++)
            {
                oos.writeObject(list[i]);
            }
        }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }
    }
    // метод для чтения списка обьектов из файла с помощью сериализации
    public void read(String filename)
    {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename)))
        {
            int index = 0;
            while (index < maxSize) {
                try {
                    add((Car) ois.readObject());
                    index++;
                } catch (EOFException e) {
                    break;
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    // метод для вывода данных о списке обьектов
    public void print()
    {
        System.out.println("Точка проката: " + point);
        System.out.println("Количество авто: " + length);
        System.out.println("Проиводитель\tМодель\tСтоимость\tКоличество");
        for(int i=0;i<length;i++)
        {
            System.out.println(list[i].toString());
        }
        System.out.println();
    }
    public void deleteDuplicates()
    {
        for(int i=0;i<length-1;i++)
        {
            for (int j=i+1;j<length;j++)
            {
                if(list[i].equals(list[j]))
                {
                    delete(j+1);
                }
            }
        }
    }
}
import java.util.Random;
public class Monte {
	public static float randFloat(float min, float max) {
	    Random rand= new Random();
	    float randomNum = rand.nextFloat() * (max - min) + min;
	    return randomNum;
	}
	public static void main(String[] args) throws Exception
	{
		float x =0 , y=0, radius = 5,pi=0,x_origin=0,y_origin=0;
		float count=0,count_inside=0;
		//Enter x_origin,y_origin,radius,slope
		System.out.println("Enter x-coordinate of the origin:");
		
		for(int i=0;i<10000;i++){
			x = randFloat(-5,5);
			y = randFloat(-5,5);
			if(x*x + y*y < radius*radius){
				count_inside++;
			}
			count++;
		}
		System.out.println("count_inside="+count_inside);
		System.out.println("count="+count);
		pi = 4*count_inside/count;
		System.out.println("pi="+pi);
	}
}

/***********/

package com.example.heatdetection;

/*
 * THE FINAL MODEL IS GAM, NEED TO FIND THESE COEFFICIENTS
 */

public class BodyTempCalc {

	public static double k_core = 0, v_core = 0, x_core = 0;
	public static int iterations = 0;
	
	public static void main(String[] args) {
		
//		System.out.println(oral_temperature(false, 12.75, 30, 1.62, 76));
		int arr[] = {145, 145, 150, 155, 160, 175, 140, 120, 180, 170};
		
		for (int i = 0; i < arr.length; i++)
			System.out.println(core_body_temp(arr[i], 37.1));
	}
	
	
	/**
	 * This isn't perfect because the input in the paper is core body temperature and
	 * the function input is oral temperature which is a mismatch in the type of temp
	 * 
	 * If we can use class variables we can utilize a single input/output 
	 * and treat it like an array so we can constantly get core body temperature 
	 * estimates from the method while allowing the update phase
	 * 
	 * @param baseline_temp - double, baseline temp taken from oral_temp. Celsius
	 * @param heart_rate - int based on reading from heart rate monitor 
	 * @return - double which is estimate of core body temperature
	 */
	public static double core_body_temp(int heart_rate, double baseline_temp) {
		double a=1, gamma=Math.pow(0.022, 2), b_0 = -7887.1, 
				b_1 = 384.4286, b_2 = -4.5714, sigma = Math.pow(18.88, 2);
		
		double x_pred, v_pred, c_vc;
		
		if (iterations == 0) {
			iterations++;
			x_pred = a * baseline_temp;
//			v_pred = Math.pow(a, 2) * v + gamma;
			v_pred = gamma;
		}
		else {
			x_pred = a * x_core;
			v_pred = Math.pow(a, 2) * v_core + gamma;
		}
		
		c_vc = 2 * b_2 * x_pred + b_1;
		k_core = (v_pred * c_vc)/ (Math.pow(c_vc, 2) * v_pred + sigma);
		x_core = x_pred + k_core * (heart_rate - (b_2 * Math.pow(x_pred, 2) + b_1 * x_pred + b_0));
		v_core = (1 - k_core * c_vc) * v_pred;
		
		
		return x_core;
	}
	
	/**
	 * Using results from study: "Defining Usual Oral Temperature Ranges in Outpatients
		Using an Unsupervised Learning Algorithm" to calculate an avergae baseline temperature
		for app user
		*Inputs: 
		* Age - int
		* sex - boolean (false = female)
		* height - float (meters)
		* weight - float (kgs)  
		******** time of day - Unsure how to do this (hour of day) ********* Fill in
		*
		* Authors removed individuals younger than 20 and older than 80
		* We can collapse any users to these ages
		* 
		* Authors removed individuals greater than 181.44kg (400lb)
		* We can collapse 
	 * @return
	 */
	public static double oral_temperature(boolean male, double hour_of_day, int age, double height, double weight) {
		// Dealing with outliers
		if (age < 20)
			age = 20;
		if (age > 80)
			age = 80;
		
		if (weight > 181)
			weight = 181;
		
		if (hour_of_day < 7)
			hour_of_day = 7;
		
		if (hour_of_day > 18)
			hour_of_day = 18;
		
		if (male)
			return male_temp_linear(hour_of_day, age, height, weight);
		else
			return female_temp_linear(hour_of_day, age, height, weight);
		
	}
	
	private static double male_temp_linear(double hour_of_day, int age, double height, double weight) {
		// Did not include int month but could

		// This is the linear model
		return 36.154 + 0.087 * hour_of_day + 0.003 * age + (-0.174 * height) + 0.001 * weight;
	}
	
	private static double female_temp_linear(double hour_of_day, int age, double height, double weight) {
		
//		return 36.256 + 0.082 * hour_of_day + 0.002 * age + (-0.111 * height) + 0.0006 * weight; //Linear
		return 36.256 + (-0.003) * hour_of_day + (-0.00005) * age + (-0.111 * height) + 0.0006 * weight; //Linear, 2
//		return 36.316 + (0.077) * hour_of_day + (0.002) * age + (-0.132 * height) + 0.0008 * weight; //Mixed effect
//		return 36.316 + (-0.002 * hour_of_day) + (-0.00005 * age) + (-0.132 * height) + (0.0008 * weight); //Mixed effect 2

	}
}

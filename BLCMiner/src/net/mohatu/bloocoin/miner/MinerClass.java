/*
 * Copyright (C) 2013 Mohatu.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package net.mohatu.bloocoin.miner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

public final class MinerClass implements Runnable {

	private String difficulty = "";

	public MinerClass(int diff) {
		for (int i = 0; i < diff; i++)
			difficulty += "0";
	}

	@Override
	public void run() {
		try {
			mine();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm found: SHA-512");
		}
	}

	public void mine() throws NoSuchAlgorithmException {
		// String testString = "dx3NAa"; //dx3NAa257363
		while (MainView.getStatus()) {
			String startString = randomString();
			System.out.println("Starting: " + startString);

			// Any reason for this 'magic' number? (10000000)
			for (int counter = 0; counter <= 10000000; counter++) {
				MainView.updateCounter();
				String currentString = startString + counter;
				String hash = DigestUtils.sha512Hex(currentString);

				if (hash.startsWith(difficulty)) {
					new Thread(new SubmitterClass(hash, currentString)).start();

					MainView.updateSolved(currentString);
					System.out.println("Success: " + currentString);

					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new FileWriter(System.getProperty("user.dir")
										+ "/BLC_Solved.txt", true)));
						out.println(currentString);
						out.close();
					} catch (IOException e) {
						// Error
						System.out
								.println("Unable to save to BLC_Solved.txt, check permissions.");
					}

				}

				if (!MainView.getStatus()) {
					counter = 10000000;
					System.out.println("STOPPING");
				}
			}
		}
	}

	public String randomString() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random r = new Random();
		int limit = 5;
		StringBuffer buf = new StringBuffer();

		buf.append(chars.charAt(r.nextInt(26)));
		for (int i = 0; i < limit; i++) {
			buf.append(chars.charAt(r.nextInt(chars.length())));
		}
		return buf.toString();
	}

	public void submit() {

	}

}

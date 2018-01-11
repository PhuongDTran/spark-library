package app.user;

import org.mindrot.jbcrypt.*;// hasing library
import static app.Application.userDao;

public class UserController {

    // Authenticate the user by hashing the inputted password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public static boolean authenticate(String username, String password) throws Exception {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.getSalt());
        return hashedPassword.equals(user.getHashedPassword());
    }

    // This method doesn't do anything, it's just included as an example
    public static void setPassword(String username, String oldPassword, String newPassword) throws Exception {
        if (authenticate(oldPassword,username)) {
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
            // Update the user salt and password
        }
    }
    
    
    //*******written by Phuong Tran*********
    
    
    // add new user
    public static void addUser(String username, String password) throws Exception
	{	
    	//generate salt and hash password
		String salt = BCrypt.gensalt();
		String hashedPassword = BCrypt.hashpw(password, salt);
		
		
		//add new user
		userDao.setUser(username, salt, hashedPassword);

	}

    
}

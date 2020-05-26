extern crate chrono;
use sysinfo::{ProcessExt, SystemExt};
use std::process::{Command, exit, Stdio, ChildStdout, ChildStderr};
use std::error::Error;
use std::{thread};
use std::sync::{Mutex, Arc};
use std::path::{PathBuf, Path};
use std::fs::{OpenOptions, File};
use std::io::{Write, Stdout, BufReader, BufRead};
use self::chrono::Utc;

pub fn check_running(s:String)-> bool {
    let mut system = sysinfo::System::new_all();
    system.refresh_all();
    for (_pid, proc) in system.get_processes() {
        if proc.name() == s {
            return true;
        }
    }
    return false;
}

pub fn start_process(s:String)->Result<String,Box<dyn Error>> {
    let mut tokens:Vec<&str>= s.split(" ").collect();
    let mut command=Command::new(tokens.first().unwrap());
    tokens.remove(0);
    for arg in tokens {
        command.arg(arg);
    }
    let running = command.spawn()?;
    Ok(String::new())
}
#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct KeepAliveParams {
    passed_attempts: u64,
    max_attempts:u64,
    active:bool,
    pub(crate) command: String
}

impl KeepAliveParams {
    pub fn start_command(&mut self) {
        let mut file = self.get_logfile();
        let clone =self.command.clone();
        let mut tokens:Vec<&str>= clone.split(" ").collect();
        let mut command=Command::new(tokens.first().unwrap());
        tokens.remove(0);
        for arg in tokens {
            command.arg(arg);
        }
        writeln!(file,"[{} PROCESS START] Start Process",Utc::now().format("%d-%m-%Y-%H:%M:%s"));
        match command.stdout(Stdio::piped()).stderr(Stdio::piped()).spawn() {
            Ok(mut child) => {
                self.read_out(child.stdout.as_mut().unwrap());
                self.read_err(child.stderr.as_mut().unwrap());

                child.wait().expect("Something goes very wrong! STOP!");
                self.passed_attempts+= 1;
                if self.passed_attempts>=self.max_attempts {
                    self.active = false;
                    writeln!(file,"[{} PROCESS STOPPED] Process ended, reached max amount of failed retries",Utc::now().format("%d-%m-%Y-%H:%M:%s"));

                } else {
                    writeln!(file,"[{} PROCESS STOPPED] Process ended, Restart...",Utc::now().format("%d-%m-%Y-%H:%M:%s"));
                    self.start_command()
                }


            }
            Err(e) => error!("Cannot start KeepAlive Process, {}",e)
        };

    }

    fn read_out(&mut self,out:&mut ChildStdout) {
        let stdout_reader = BufReader::new(out);
        let stdout_lines = stdout_reader.lines();

        for line in stdout_lines {
            self.append_out_to_log(line.unwrap())
        }
    }

    fn read_err(&mut self,err:&mut ChildStderr) {
        let stdout_reader = BufReader::new(err);
        let stdout_lines = stdout_reader.lines();

        for line in stdout_lines {
            self.append_err_to_log(line.unwrap())
        }
    }

    fn get_logfile (&mut self) -> File {
        let folder =  std::env::current_exe().ok().expect("Error while open configuration Folder");
        let folder = folder.parent().unwrap();
        let mut path_buf = PathBuf::new();
        path_buf.push(folder.parent().unwrap());
        path_buf.push("logs");
        path_buf.push(self.clone().command.replace("/"," "));
        path_buf.set_extension("log");
        let path = path_buf.as_path();
        match KeepAliveParams::create_log_file(path) {
            Err(e) => {
                error!("Cannot write LogFile because for {} {}",self.clone().command,e);
                exit(1);
            }
            _ => ()
        };
        let  file = OpenOptions::new().append(true).write(true).open(path).unwrap();
        return file;
    }

    fn create_log_file(path:&Path)-> Result<(),Box<dyn Error>> {
        std::fs::create_dir_all(path.parent().unwrap())?;
        if !path.exists() {
            File::create(path)?;
        }
        Ok(())
    }

    fn append_out_to_log ( &mut self,str:String) {
        let mut file = self.get_logfile();
        writeln!(file,"[{} INFO] {}",Utc::now().format("%d-%m-%Y-%H:%M:%s"),str);
    }

    fn append_err_to_log ( &mut self,str:String) {
        let mut file = self.get_logfile();
        writeln!(file,"[{} ERROR] {}",Utc::now().format("%d-%m-%Y-%H:%M:%s"),str);
    }

    pub fn reset(&mut self) {
        self.passed_attempts = 0;
        self.active = true;
    }

    pub fn restart (&mut self) {
        self.start_command();
    }

}
pub struct KeepAlive {
    pub(crate) params: Arc<Mutex<KeepAliveParams>>
}

impl KeepAlive {
    pub fn new(attempts:u64, command:String)-> KeepAlive {
        KeepAlive{
            params: Arc::new(Mutex::new(KeepAliveParams {
                passed_attempts : 0,
                max_attempts : attempts,
                active: true,
                command:command,
            }))
    }}


    pub fn start(&mut self) {
        let local_self = self.params.clone();
        thread::spawn (move || {
            local_self.lock().unwrap().start_command();
        });
    }


}




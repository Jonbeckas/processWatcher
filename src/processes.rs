use sysinfo::{ProcessExt, SystemExt};
use std::process::Command;
use std::error::Error;

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
    let mut counter =0;
    for arg in tokens {
        command.arg(arg);
        counter =counter+1;

    }
    let output = command.output()?;
    unsafe {
       Ok(String::from_utf8_unchecked(output.stdout))
    }
}
//
//  XBridgeTableViewController.m
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 10/6/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import "XBridgeTableViewController.h"

#import "SensorPositionList.h"

@interface XBridgeTableViewController ()

@end

@implementation XBridgeTableViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    self.clearsSelectionOnViewWillAppear = NO;
    
    defaults = [NSUserDefaults standardUserDefaults];
    
    sensorPositionArray = @[@"HORIZONTAL BUT. LEFT",@"HORIZONTAL BUT. RIGHT",@"HORIZONTAL INVERTED BUT. LEFT",@"HORIZONTAL INVERTED BUT. RIGHT",@"VERTICAL BUT. DOWN",@"VERTICAL BUT. UP",@"VERTICAL INVERTED BUT. UP",@"VERTICAL INVERTED BUT. DOWN"];
    UINib *nib = [UINib nibWithNibName:@"SensorPositionList" bundle:nil];
    [self.tableView registerNib:nib forCellReuseIdentifier:@"SensorPositionCell"];
    
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    self.tableView.estimatedRowHeight = 100.0;
    
    [self.tableView reloadData];
}

- (void)viewDidUnload{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSInteger nCount = [sensorPositionArray count];
    return nCount;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *MyIdentifier = @"SensorPositionCell";
    SensorPositionList *cell = (SensorPositionList*)[tableView dequeueReusableCellWithIdentifier:MyIdentifier];
    
    cell.sensorPosLabel.text = [NSString stringWithFormat:@"%@", [sensorPositionArray objectAtIndex:indexPath.row]];
    
    if(indexPath.row == [[defaults objectForKey:@"hpg_sensor_position"] intValue]){
        [tableView selectRowAtIndexPath:indexPath animated:FALSE scrollPosition:UITableViewScrollPositionNone];
        [[tableView delegate] tableView:tableView didSelectRowAtIndexPath:indexPath];
        
    }
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    actualSensorPosition = (int)indexPath.row;
    
    [defaults setObject:[NSNumber numberWithInt:actualSensorPosition] forKey:@"hpg_sensor_position"];
    [defaults synchronize];
    
}

@end

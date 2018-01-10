#!/usr/bin/env perl

use warnings;
use strict;

use REST::Client;
 
# from: http://search.cpan.org/~mcrawfor/REST-Client-88/lib/REST/Client.pm

my @mikes = ['MICHAEL','MICKIE','MIKE','MICKY','MIKEL','MICHEAL','MICK','MICKI','MIQUEL','MICHEL','MIGUEL','MICHALE','MITCHEL','MICHAL','MITCHELL','MICKEY','MITCH'];
 
my $client = REST::Client->new();
 
$client->POST('http://localhost:8181/ws/auth/auth/authenticate', 'username:ngs
password:ngs');
my $resp = $client->responseContent();
print $resp;

$client->GET('http://localhost:8181/ws/mm/PersonRS/sbrs/1005043886');
my $resp = $client->responseContent();
print $resp;
 
my %reqTemp = (
  "Person" => (
    "DOB" => "1945-12-04T00:00:00",
    "FirstName" => "MIKE",
    "Gender" => "M",
    "LastName" => "SMITH",
    "SSN" => "772023479"
  ),
  "Status" => "active"
);
  
 # #A host can be set for convienience
 # $client->setHost('http://example.com');
 # $client->PUT('/dir/file.xml', '<example>new content</example>');
 # if( $client->responseCode() eq '200' ){
     # print "Deleted\n";
 # }
  
 # #custom request headers may be added
 # $client->addHeader('CustomHeader', 'Value');
  
 # #response headers may be gathered
 # print $client->responseHeader('ResponseHeader');
  
 # #X509 client authentication
 # $client->setCert('/path/to/ssl.crt');
 # $client->setKey('/path/to/ssl.key');
  
 # #add a CA to verify server certificates
 # $client->setCa('/path/to/ca.file');
  
 # #you may set a timeout on requests, in seconds
 # $client->setTimeout(10);
  
 # #options may be passed as well as set
 # $client = REST::Client->new({
         # host    => 'https://example.com',
         # cert    => '/path/to/ssl.crt',
         # key     => '/path/to/ssl.key',
         # ca      => '/path/to/ca.file',
         # timeout => 10,
     # });
 # $client->GET('/dir/file', {CustomHeader => 'Value'});
  
 # #Requests can be specificed directly as well
 # $client->request('GET', '/dir/file', 'request body content', {CustomHeader => 'Value'});